package com.redislabs.rediscogs;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.redislabs.rediscogs.loader.EntityType;
import com.redislabs.rediscogs.loader.RediSearchConfiguration;

import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.client.SuggestionOptions;
import io.redisearch.client.SuggestionOptions.With;
import lombok.Builder;
import lombok.Data;

@RestController
class RediscogsController {

	@Autowired
	private ServerConfiguration config;

	@Autowired
	private RediSearchConfiguration rediSearchConfig;

	@Autowired
	private ImageRepository imageRepository;

	private String artistQueryPattern = "{0} {1} @artistId:{2}";
	private String queryPattern = "{0} {1}";

	@Data
	@Builder
	public static class ArtistSuggestion {
		private String name;
		private String id;
	}

	@GetMapping("/suggest-artists")
	public Stream<ArtistSuggestion> suggestArtists(
			@RequestParam(name = "prefix", defaultValue = "", required = false) String prefix) {
		SuggestionOptions options = SuggestionOptions.builder().with(With.PAYLOAD).max(10).build();
		List<Suggestion> results = rediSearchConfig.getSuggestClient(EntityType.Artists.id()).getSuggestion(prefix,
				options);
		return results.stream()
				.map(result -> ArtistSuggestion.builder().id(result.getPayload()).name(result.getString()).build());
	}

	@GetMapping("/search-albums")
	public Stream<Map<String, Object>> searchAlbums(@RequestParam(name = "artistId", required = false) String artistId,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		String queryPattern = getQueryPattern(artistId);
		Query q = new Query(MessageFormat.format(queryPattern, config.getImageFilter(), query, artistId));
		q.limit(0, config.getSearchResultsLimit());
		q.setSortBy("year", true);
		SearchResult results = rediSearchConfig.getSearchClient(EntityType.Masters.id()).search(q);
		return results.docs.stream().map(doc -> toMap(doc.getProperties()));
	}

	private Map<String, Object> toMap(Iterable<Entry<String, Object>> properties) {
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : properties) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	private String getQueryPattern(String artistId) {
		if (artistId == null || artistId.length() == 0) {
			return queryPattern;
		}
		return artistQueryPattern;
	}

	@ResponseBody
	@GetMapping(value = "/album-image/{id}")
	public ResponseEntity<byte[]> getImageAsResource(@PathVariable("id") long masterId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		return new ResponseEntity<>(imageRepository.getImage(masterId), headers, HttpStatus.OK);
	}

}
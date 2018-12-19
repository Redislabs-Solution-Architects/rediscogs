package com.redislabs.rediscogs.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.ruaux.jdiscogs.JDiscogsConfiguration;
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

import com.redislabs.springredisearch.RediSearchConfiguration;

import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.client.Client;
import io.redisearch.client.SuggestionOptions;
import io.redisearch.client.SuggestionOptions.With;
import lombok.Builder;
import lombok.Data;

@RestController
class RediscogsController {

	@Autowired
	private ServerConfiguration config;
	@Autowired
	private JDiscogsConfiguration discogs;
	@Autowired
	private RediSearchConfiguration rediSearchConfig;
	@Autowired
	private ImageRepository imageRepository;

	private Client artistSuggestClient;
	private Client masterClient;

	@Data
	@Builder
	public static class ArtistSuggestion {
		private String name;
		private String id;
	}

	@PostConstruct
	public void init() {
		masterClient = rediSearchConfig.getClient(discogs.getData().getMasterIndex());
		artistSuggestClient = rediSearchConfig.getClient(discogs.getData().getArtistSuggestionIndex());
	}

	@GetMapping("/suggest-artists")
	public Stream<ArtistSuggestion> suggestArtists(
			@RequestParam(name = "prefix", defaultValue = "", required = false) String prefix) {
		SuggestionOptions options = SuggestionOptions.builder().with(With.PAYLOAD).max(10).build();
		List<Suggestion> results = artistSuggestClient.getSuggestion(prefix, options);
		return results.stream()
				.map(result -> ArtistSuggestion.builder().id(result.getPayload()).name(result.getString()).build());
	}

	@GetMapping("/search-albums")
	public Stream<Map<String, Object>> searchAlbums(@RequestParam(name = "artistId", required = false) String artistId,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		Query q = new Query(getQuery(query, artistId));
		q.limit(0, config.getSearchResultsLimit());
		q.setSortBy("year", true);
		SearchResult results = masterClient.search(q);
		return results.docs.stream().map(doc -> toMap(doc));
	}

	private Map<String, Object> toMap(Document doc) {
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : doc.getProperties()) {
			map.put(entry.getKey(), entry.getValue());
		}
		map.put("id", doc.getId());
		return map;
	}

	private String getQuery(String queryString, String artistId) {
		String query = config.getImageFilter() + " " + queryString;
		if (artistId != null && artistId.length() > 0) {
			String artistFilter = config.getArtistIdFilter().replace("{artistId}", artistId);
			return query + " " + artistFilter;
		}
		return query;
	}

	@ResponseBody
	@GetMapping(value = "/album-image/{id}")
	public ResponseEntity<byte[]> getImageAsResource(@PathVariable("id") String masterId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		return new ResponseEntity<>(imageRepository.getImage(masterId), headers, HttpStatus.OK);
	}

}
package com.redislabs.rediscogs;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

import io.redisearch.Document;
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
	private RediscogsConfiguration config;

	@Autowired
	private RediSearchClientConfiguration rediSearchConfig;

	@Autowired
	private MasterRepository masterRepository;

	@Autowired
	private ImageRepository imageRepository;

	private Optional<RedisMaster> getRedisMaster(Document doc) {
		String id = doc.getId();
		if (doc.getId() != null && doc.getId().length() > 0) {
			return masterRepository.findById(id);
		}
		return Optional.empty();
	}

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
		List<Suggestion> results = rediSearchConfig.getClient(config.getArtistsSuggestionIndex()).getSuggestion(prefix,
				options);
		return results.stream()
				.map(result -> ArtistSuggestion.builder().id(result.getPayload()).name(result.getString()).build());
	}

	@GetMapping("/search-albums")
	public Stream<RedisMaster> searchAlbums(@RequestParam(name = "artistId", required = false) String artistId,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		String queryString = query;
		if (artistId != null) {
			queryString += " " + "@artistId:" + artistId;
		}
		Query q = new Query(queryString);
		q.limit(0, config.getSearchResultsLimit());
		q.setSortBy("year", true);
		SearchResult results = rediSearchConfig.getClient(config.getMastersIndex()).search(q);
		return results.docs.stream().map(doc -> getRedisMaster(doc)).filter(Optional::isPresent).map(Optional::get);
	}

	@ResponseBody
	@GetMapping(value = "/album-image/{id}")
	public ResponseEntity<byte[]> getImageAsResource(@PathVariable("id") String masterId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		return new ResponseEntity<>(imageRepository.getImage(masterId), headers, HttpStatus.OK);
	}

}
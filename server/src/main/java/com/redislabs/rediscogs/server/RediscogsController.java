package com.redislabs.rediscogs.server;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.lettusearch.search.Limit;
import com.redislabs.lettusearch.search.SearchOptions;
import com.redislabs.lettusearch.search.SearchResult;
import com.redislabs.lettusearch.search.SearchResults;
import com.redislabs.lettusearch.search.SortBy;
import com.redislabs.lettusearch.search.SortBy.Direction;
import com.redislabs.lettusearch.suggest.SuggestGetOptions;
import com.redislabs.lettusearch.suggest.SuggestResult;

import lombok.Builder;
import lombok.Data;

@RestController
class RediscogsController {

	@Autowired
	private ServerConfiguration config;
	@Autowired
	private JDiscogsConfiguration discogs;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private RediSearchClient client;
	private StatefulRediSearchConnection<String, String> connection;

	@PostConstruct
	public void init() {
		connection = client.connect();
	}

	@PreDestroy
	public void tearDown() {
		if (connection != null) {
			connection.close();
		}
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
		List<SuggestResult<String>> results = connection.sync().sugget(discogs.getData().getArtistSuggestionIndex(),
				prefix, SuggestGetOptions.builder().withPayloads(true).max(20l).build());
		return results.stream()
				.map(result -> ArtistSuggestion.builder().id(result.getPayload()).name(result.getString()).build());
	}

	@GetMapping("/search-albums")
	public Stream<Map<String, String>> searchAlbums(@RequestParam(name = "artistId", required = false) String artistId,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		SearchResults<String, String> results = connection.sync().search(discogs.getData().getMasterIndex(),
				getQuery(query, artistId),
				SearchOptions.builder().limit(Limit.builder().num(config.getSearchResultsLimit()).build())
						.sortBy(SortBy.builder().field("year").direction(Direction.Ascending).build()).build());
		return results.getResults().stream().map(result -> toMap(result));
	}

	private Map<String, String> toMap(SearchResult<String, String> result) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("id", result.getDocumentId());
		map.putAll(result.getFields());
		return map;
	}

	private String getQuery(String queryString, String artistId) {
		String query = config.getImageFilter();
		if (artistId != null && artistId.length() > 0) {
			String artistFilter = config.getArtistIdFilter().replace("{artistId}", artistId);
			query += " " + artistFilter;
		}
		if (queryString != null && queryString.length() > 0) {
			query += " " + queryString;
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
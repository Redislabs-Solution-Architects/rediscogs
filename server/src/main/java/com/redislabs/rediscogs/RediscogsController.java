package com.redislabs.rediscogs;

import java.io.IOException;
import java.util.Map;
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

import com.redislabs.lettusearch.RediSearchConnection;
import com.redislabs.lettusearch.index.Limit;
import com.redislabs.lettusearch.index.SearchOptions;
import com.redislabs.lettusearch.index.SearchOptions.SearchOptionsBuilder;
import com.redislabs.lettusearch.index.SearchResult;
import com.redislabs.lettusearch.index.SearchResultsNoContent;
import com.redislabs.lettusearch.index.SortBy;
import com.redislabs.lettusearch.index.SortBy.Direction;
import com.redislabs.lettusearch.suggest.SuggestionOptions;

import lombok.Builder;
import lombok.Data;

@RestController
class RediscogsController {

	@Autowired
	private RediscogsConfiguration config;

	@Autowired
	private MasterRepository masterRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private RediSearchConnection<String, String> connection;

	private Optional<RedisMaster> getRedisMaster(SearchResult<String, String> doc) {
		String id = doc.getDocumentId();
		if (id != null && id.length() > 0) {
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
		Map<String, String> suggestions = connection.sync().suggestionGetWithPayloads(
				config.getArtistsSuggestionIndex(), prefix, SuggestionOptions.builder().maxResults(10).build());
		return suggestions.entrySet().stream()
				.map(result -> ArtistSuggestion.builder().name(result.getKey()).id(result.getValue()).build());
	}

	@GetMapping("/search-albums")
	public Stream<RedisMaster> searchAlbums(@RequestParam(name = "artistId", required = false) String artistId,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		String queryString = query;
		if (artistId != null) {
			queryString += " " + "@artistId:" + artistId;
		}
		SearchOptionsBuilder builder = SearchOptions.builder();
		builder.limit(Limit.builder().offset(0).num(config.getSearchResultsLimit()).build());
		builder.sortBy(SortBy.builder().field("year").direction(Direction.Ascending).build());
		SearchResultsNoContent<String, String> results = connection.sync().searchNoContent(config.getMastersIndex(),
				queryString, builder.build());
		return results.getResults().stream().map(doc -> getRedisMaster(doc)).filter(Optional::isPresent)
				.map(Optional::get);
	}

	@ResponseBody
	@GetMapping(value = "/album-image/{id}")
	public ResponseEntity<byte[]> getImageAsResource(@PathVariable("id") String masterId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		return new ResponseEntity<>(imageRepository.getImage(masterId), headers, HttpStatus.OK);
	}

}
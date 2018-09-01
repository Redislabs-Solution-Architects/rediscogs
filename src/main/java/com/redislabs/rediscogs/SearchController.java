package com.redislabs.rediscogs;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.client.SuggestionOptions;

@RestController
class SearchController {

	@Autowired
	private RediSearchClientConfiguration rediSearchConfig;

	@Autowired
	private RediscogsConfiguration config;

	@GetMapping("/search-masters")
	public Stream<Iterable<Map.Entry<String, Object>>> searchMasters(@RequestParam("query") String queryString) {
		Query query = new Query(queryString);
		SearchResult result = rediSearchConfig.getClient(config.getMastersIndex()).search(query);
		return result.docs.stream().map(doc -> doc.getProperties());
	}

	@GetMapping("/suggest-artists")
	public Stream<String> suggestArtists(@RequestParam("prefix") String prefix) {
		SuggestionOptions options = SuggestionOptions.builder().fuzzy().build();
		List<Suggestion> results = rediSearchConfig.getClient(config.getArtistsSuggestionIdx()).getSuggestion(prefix,
				options);
		return results.stream().map(result -> result.getString());
	}

}
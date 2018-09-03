package com.redislabs.rediscogs;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.redisearch.Document;
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

	@Autowired
	private MasterRepository repository;

	private RestTemplate restTemplate;

	public SearchController(RestTemplateBuilder restTemplateBuilder) {
		restTemplate = restTemplateBuilder.build();
	}

	@GetMapping("/search-albums")
	public List<RedisMaster> searchAlbums(@RequestParam("query") String queryString) {
		Query query = new Query(queryString);
		SearchResult results = rediSearchConfig.getClient(config.getMastersIndex()).search(query);
		List<RedisMaster> masters = new ArrayList<RedisMaster>();
		for (Document doc : results.docs) {
			String id = doc.getId();
			if (id != null && !id.equals("")) {
				Optional<RedisMaster> optional = repository.findById(id);
				if (optional.isPresent()) {
					RedisMaster master = optional.get();
					if (master.getImageUri() == null) {
						MasterEntity response = getDiscogsMaster(master.getId());
						if (response.getImages() != null && response.getImages().size() > 0) {
							master.setImageUri(response.getImages().get(0).getUri());
							master.setImageUri150(response.getImages().get(0).getUri150());
							repository.save(master);
						}
					}
					masters.add(master);
				}
			}
		}
		return masters;
	}

	private MasterEntity getDiscogsMaster(String id) {
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("id", id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(config.getDiscogsApiUrl()).queryParam("token",
				config.getDiscogsApiToken());
		URI uri = builder.buildAndExpand(uriParams).toUri();
		return restTemplate.getForObject(uri, MasterEntity.class);
	}

	@GetMapping("/suggest-artists")
	public Stream<String> suggestArtists(@RequestParam("prefix") String prefix) {
		SuggestionOptions options = SuggestionOptions.builder().fuzzy().build();
		List<Suggestion> results = rediSearchConfig.getClient(config.getArtistsSuggestionIdx()).getSuggestion(prefix,
				options);
		return results.stream().map(result -> result.getString());
	}

}
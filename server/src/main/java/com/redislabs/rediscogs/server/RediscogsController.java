package com.redislabs.rediscogs.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.ruaux.jdiscogs.JDiscogsConfiguration;
import org.ruaux.jdiscogs.data.MasterIndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
class RediscogsController {

	@Autowired
	private RediscogsConfiguration config;
	@Autowired
	private JDiscogsConfiguration discogs;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private StatefulRediSearchConnection<String, String> connection;

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

	@PostMapping("/favorite-album")
	public ResponseEntity<Void> favoriteAlbum(@RequestBody Album album, HttpSession session) {
		String username = (String) session.getAttribute(config.getUsernameAttribute());
		log.info("Received favorite id {} from user {}", album.getId(), username);
		connection.sync().xadd(config.getFavoritesStream(), "user", username, "id", album.getId());
		Set<String> favorites = (Set<String>) session.getAttribute(config.getFavoritesAttribute());
		if (favorites == null) {
			favorites = new LinkedHashSet<>();
		}
		favorites.add(album.getId());
		session.setAttribute(config.getFavoritesAttribute(), favorites);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/username")
	public ResponseEntity<Void> username(@RequestBody String username, HttpSession session) {
		log.info("Setting username '{}'", username);
		session.setAttribute(config.getUsernameAttribute(), username);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/search-albums")
	public Stream<Album> searchAlbums(HttpSession session,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		SearchResults<String, String> results = connection.sync().search(discogs.getData().getMasterIndex(), query,
				SearchOptions.builder().limit(Limit.builder().num(config.getSearchResultsLimit()).build())
						.sortBy(SortBy.builder().field("year").direction(Direction.Ascending).build()).build());
		return results.getResults().stream().map(result -> createAlbum(session, result));
	}

	private Album createAlbum(HttpSession session, SearchResult<String, String> result) {
		Album album = new Album();
		album.setId(result.getDocumentId());
		album.setArtist(result.getFields().get(MasterIndexWriter.FIELD_ARTIST));
		album.setArtistId(result.getFields().get(MasterIndexWriter.FIELD_ARTISTID));
		album.setTitle(result.getFields().get(MasterIndexWriter.FIELD_TITLE));
		album.setYear(result.getFields().get(MasterIndexWriter.FIELD_YEAR));
		album.setGenres(Arrays.asList(result.getFields().getOrDefault(MasterIndexWriter.FIELD_GENRES, "")
				.split(discogs.getHashArrayDelimiter())));
		Set<String> favorites = (Set<String>) session.getAttribute(config.getFavoritesAttribute());
		if (favorites != null) {
			album.setFavorite(favorites.contains(album.getId()));
		}
		return album;
	}

	@ResponseBody
	@GetMapping(value = "/album-image/{id}")
	public ResponseEntity<byte[]> getImageAsResource(@PathVariable("id") String masterId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		return new ResponseEntity<>(imageRepository.getImage(masterId), headers, HttpStatus.OK);
	}

}
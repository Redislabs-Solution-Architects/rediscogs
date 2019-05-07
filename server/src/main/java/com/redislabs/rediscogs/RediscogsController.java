package com.redislabs.rediscogs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.ruaux.jdiscogs.JDiscogsConfiguration;
import org.ruaux.jdiscogs.data.MasterIndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.redislabs.rediscogs.RediscogsConfiguration.StompConfig;
import com.redislabs.rediscogs.model.Album;
import com.redislabs.rediscogs.model.AlbumLike;
import com.redislabs.rediscogs.model.ArtistSuggestion;
import com.redislabs.rediscogs.model.LikeHistory;
import com.redislabs.rediscogs.model.User;

import io.lettuce.core.Range;
import io.lettuce.core.StreamMessage;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@SuppressWarnings("unchecked")
class RediscogsController {

	@Autowired
	private RediscogsConfiguration config;
	@Autowired
	private JDiscogsConfiguration discogs;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private StatefulRediSearchConnection<String, String> connection;
	@Autowired
	private AlbumMarshaller marshaller;

	@GetMapping("/stomp-config")
	public StompConfig stompConfig() {
		return config.getStomp();
	}

	@GetMapping("/suggest-artists")
	public Stream<ArtistSuggestion> suggestArtists(
			@RequestParam(name = "prefix", defaultValue = "", required = false) String prefix) {
		List<SuggestResult<String>> results = connection.sync().sugget(discogs.getData().getArtistSuggestionIndex(),
				prefix, SuggestGetOptions.builder().withPayloads(true).max(20l).build());
		return results.stream().map(result -> artistSuggestion(result));
	}

	private ArtistSuggestion artistSuggestion(SuggestResult<String> result) {
		ArtistSuggestion suggestion = new ArtistSuggestion();
		suggestion.setId(result.getPayload());
		suggestion.setName(result.getString());
		return suggestion;
	}

	@PostMapping("/like-album")
	public ResponseEntity<Void> likeAlbum(@RequestBody Album album, HttpSession session) {
		User user = (User) session.getAttribute(config.getUserAttribute());
		Map<String, String> fields = new HashMap<>();
		if (user != null) {
			fields.put(config.getUserAttribute(), user.getName());
		}
		fields.put(MasterIndexWriter.FIELD_ID, album.getId());
		fields.put(MasterIndexWriter.FIELD_ARTIST, album.getArtist());
		fields.put(MasterIndexWriter.FIELD_ARTISTID, album.getArtistId());
		fields.put(MasterIndexWriter.FIELD_GENRES, String.join(discogs.getHashArrayDelimiter(), album.getGenres()));
		fields.put(MasterIndexWriter.FIELD_TITLE, album.getTitle());
		fields.put(MasterIndexWriter.FIELD_YEAR, album.getYear());
		connection.sync().xadd(config.getLikesStream(), fields);
		Set<String> likes = (Set<String>) session.getAttribute(config.getLikesAttribute());
		if (likes == null) {
			likes = new LinkedHashSet<>();
		}
		likes.add(album.getId());
		session.setAttribute(config.getLikesAttribute(), likes);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/user")
	public ResponseEntity<Void> setUsername(@RequestBody User user, HttpSession session) {
		log.info("Setting user '{}'", user.getName());
		session.setAttribute(config.getUserAttribute(), user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/user")
	public User user(HttpSession session) {
		return (User) session.getAttribute(config.getUserAttribute());
	}

	@GetMapping("/likes")
	public LikeHistory likes() {
		List<AlbumLike> likes = new ArrayList<>();
		List<StreamMessage<String, String>> messages = connection.sync().xrevrange(config.getLikesStream(),
				Range.unbounded(), io.lettuce.core.Limit.create(0, config.getMaxLikes()));
		for (StreamMessage<String, String> message : messages) {
			likes.add(marshaller.albumLike(message));
		}
		LikeHistory history = new LikeHistory();
		history.setLikes(likes);
		return history;
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
		Set<String> likes = (Set<String>) session.getAttribute(config.getLikesAttribute());
		if (likes != null) {
			album.setLike(likes.contains(album.getId()));
		}
		return album;
	}

	@GetMapping(value = "/album-image/{id}")
	public void getImageAsResource(@PathVariable("id") String masterId, HttpServletResponse response)
			throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		IOUtils.copy(new ByteArrayInputStream(imageRepository.getImage(masterId)), response.getOutputStream());
	}

}
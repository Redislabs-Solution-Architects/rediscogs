package com.redislabs.rediscogs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.redislabs.rediscogs.RediscogsProperties.StompConfig;
import com.redislabs.rediscogs.model.Album;
import com.redislabs.rediscogs.model.Like;
import com.redislabs.rediscogs.model.User;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
@CrossOrigin
@Slf4j
@SuppressWarnings("unchecked")
class RediscogsController {

	@Autowired
	private RediscogsProperties config;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private LikeService likeService;
	@Autowired
	private SearchService searchService;

	@GetMapping("/config/stomp")
	public StompConfig stompConfig() {
		return config.getStomp();
	}

	@Data
	@Builder
	public static class ArtistSuggestion {
		private String name;
		private String id;
	}

	@GetMapping("/suggest/artists")
	public Stream<ArtistSuggestion> suggestArtists(
			@RequestParam(name = "prefix", defaultValue = "", required = false) String prefix) {
		return searchService.suggestArtists(prefix);
	}

	@PostMapping("/likes/album")
	public ResponseEntity<Void> like(@RequestBody Album album, HttpSession session,
			@RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
		Set<String> likes = (Set<String>) session.getAttribute(config.getLikesAttribute());
		if (likes == null) {
			likes = new LinkedHashSet<>();
		}
		likes.add(album.getId());
		session.setAttribute(config.getLikesAttribute(), likes);
		likeService.like(album, (User) session.getAttribute(config.getUserAttribute()), userAgent);
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
	public Stream<Like> likes() {
		return likeService.likes();
	}

	@GetMapping("/search/albums")
	public List<Album> searchAlbums(HttpSession session,
			@RequestParam(name = "query", required = false, defaultValue = "") String query) {
		List<Album> albums = searchService.searchAlbums(query).collect(Collectors.toList());
		Set<String> likes = (Set<String>) session.getAttribute(config.getLikesAttribute());
		if (likes != null) {
			albums.forEach(album -> album.setLike(likes.contains(album.getId())));
		}
		return albums;
	}

	@GetMapping(value = "/image/album/{id}")
	public void getImageAsResource(@PathVariable("id") String masterId, HttpServletResponse response)
			throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		byte[] buffer = imageRepository.getImage(masterId);
		if (buffer != null) {
			IOUtils.copy(new ByteArrayInputStream(buffer), response.getOutputStream());
		}
	}

}
package com.redislabs.rediscogs.discogs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.redislabs.rediscogs.EntityType;
import com.redislabs.rediscogs.ImageRepository;
import com.redislabs.rediscogs.RediscogsConfiguration;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisImageRepository implements ImageRepository {

	@Autowired
	private RediscogsConfiguration config;
	private RestTemplate restTemplate;
	private long rateLimitLastTime;
	private int rateLimitRemaining;

	public RedisImageRepository(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	@Cacheable(value = "images", unless = "#result == null")
	public byte[] getImage(String masterId) {
		DiscogsMaster response = getDiscogsMaster(masterId);
		if (response != null && response.getImages() != null && response.getImages().size() > 0) {
			String uriString = response.getImages().get(0).getUri();
			try {
				URL url = new URL(uriString);
				try (BufferedInputStream in = new BufferedInputStream(url.openStream());
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
					byte dataBuffer[] = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
						outputStream.write(dataBuffer, 0, bytesRead);
					}
					return outputStream.toByteArray();
				} catch (IOException e) {
					log.error("Could not read stream from URL: {}", url, e);
				}
			} catch (MalformedURLException e) {
				log.error("Invalid URL: {}", uriString, e);
			}
		}
		return null;
	}

	private synchronized DiscogsMaster getDiscogsMaster(String id) {
		log.info("RateLimitRemaining: {}", rateLimitRemaining);
		try {
			Thread.sleep(config.getDiscogs().getDelay());
		} catch (InterruptedException e) {
			// do nothing
		}
		boolean after1Min = (System.currentTimeMillis() - rateLimitLastTime) > 60000;
		if (rateLimitRemaining > 1 || after1Min) {
			Map<String, String> uriParams = new HashMap<String, String>();
			uriParams.put("entity", EntityType.Masters.id());
			uriParams.put("id", id);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(config.getDiscogs().getUrl())
					.queryParam("token", config.getDiscogs().getToken());
			URI uri = builder.buildAndExpand(uriParams).toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.set("User-Agent", config.getDiscogs().getUserAgent());
			RequestEntity<Object> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
			ResponseEntity<DiscogsMaster> response = restTemplate.exchange(requestEntity, DiscogsMaster.class);
			HttpHeaders responseHeaders = response.getHeaders();
			int newRemaining = Integer.parseInt(responseHeaders.get("X-Discogs-Ratelimit-Remaining").get(0));
			this.rateLimitLastTime = System.currentTimeMillis();
			if (rateLimitRemaining == 0 || newRemaining < rateLimitRemaining || after1Min) {
				log.info("RateLimitRemaining -> {}", newRemaining);
				this.rateLimitRemaining = newRemaining;
			}
			return response.getBody();
		}
		return null;
	}
}

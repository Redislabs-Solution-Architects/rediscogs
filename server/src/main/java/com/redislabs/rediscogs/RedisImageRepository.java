package com.redislabs.rediscogs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.ruaux.jdiscogs.api.DiscogsClient;
import org.ruaux.jdiscogs.api.model.Master;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisImageRepository implements ImageRepository {

	@Autowired
	private RediscogsConfiguration config;
	@Autowired
	private DiscogsClient discogs;

	@Override
	@Cacheable(value = "images", unless = "#result == null")
	public byte[] getImage(String masterId) {
		if (config.getImageDelay() > 0) {
			try {
				Thread.sleep(config.getImageDelay());
			} catch (InterruptedException e) {
				log.warn("Sleep interrupted", e);
				return null;
			}
		}
		Master response = discogs.getMaster(masterId);
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
				log.error("Invalid URL: {}", uriString);
			}
		}
		return null;
	}

}

package com.redislabs.rediscogs.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@Data
public class ServerConfiguration {

	private int searchResultsLimit = 20;
	private String imageFilter;
	private Discogs discogs;

	@Data
	public static class Discogs {
		private String url;
		private String token;
		private String userAgent;
		private long delay = 100;
	}

}
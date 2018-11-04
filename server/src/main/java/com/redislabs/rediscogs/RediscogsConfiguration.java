package com.redislabs.rediscogs;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@Data
public class RediscogsConfiguration {

	boolean skipLoad = false;
	private int batchSize = 50;
	private EntityType[] entities = { EntityType.Masters };
	private String entityPathTemplate;
	private String redisearchHost;
	private Integer redisearchPort;
	private int searchResultsLimit = 20;
	private String imageFilter;
	private String hashArrayDelimiter = " ";
	private Discogs discogs;

	public String getIndexName(EntityType type) {
		return type.id() + "Idx";
	}

	public String getSuggestIndexName(EntityType type) {
		return type.id() + "SuggestIdx";
	}

	@Data
	public static class Discogs {
		private String url;
		private String token;
		private String userAgent;
		private String fileUrlTemplate;
		private long delay = 100;
	}

}
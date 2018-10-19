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
	private String mastersFile;
	private String mastersIndex = "mastersIdx";
	private String artistsSuggestionIndex = "artistsSuggestionIdx";
	private String discogsApiUrl;
	private String discogsApiToken;
	private String discogsApiUserAgent;
	private long discogsApiDelay = 100;
	private String redisearchHost;
	private Integer redisearchPort;
	private int searchResultsLimit = 20;
	private String imageFilter;

}
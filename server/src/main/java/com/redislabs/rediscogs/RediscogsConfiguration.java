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

	boolean skipLoad;
	private int batchSize;
	private String mastersFile;
	private String mastersIndex;
	private String artistsSuggestionIndex;
	private String discogsApiUrl;
	private String discogsApiToken;
	private long discogsApiDelay;
	private String discogsApiUserAgent;
	private String redisearchHost;
	private Integer redisearchPort;
	private int searchResultsLimit;

}
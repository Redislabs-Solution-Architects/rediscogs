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
	private String mastersIndex = "mastersIdx";
	private String artistsSuggestionIndex = "artistsSuggestionIdx";
	private String mastersFile;
	private String discogsApiUrl;
	private String discogsApiToken;
	private String userAgent;
	private int batchSize = 50;
	private int searchResultsLimit = 20;
	private String defaultImageUri;
	private String rediSearchHost;
	private Integer rediSearchPort;

}
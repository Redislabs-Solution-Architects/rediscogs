package com.redislabs.rediscogs.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@Data
public class RediscogsConfiguration {

	private int searchResultsLimit = 20;
	private long imageDelay = 3000;
	private String usernameAttribute = "username";
	private String favoritesAttribute = "favorites";
	private String favoritesStream = "favorites:stream";

}
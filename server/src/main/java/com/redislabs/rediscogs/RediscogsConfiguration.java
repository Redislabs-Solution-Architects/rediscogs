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

	private int searchResultsLimit = 20;
	private long imageDelay = 3000;
	private String userAttribute = "username";
	private String likesAttribute = "likes";
	private String websocketEndpoint = "/websocket";
	private String websocketDestinationPrefix = "/topic";
	private String likesTopic = websocketDestinationPrefix + "/likes";
	private String likesStream = "likes:stream";
	private int maxLikes = 10;

}
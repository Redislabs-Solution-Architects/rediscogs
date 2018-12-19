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

	private String imageFilter;
	private String artistIdFilter;
	private int searchResultsLimit = 20;
	private long imageDelay = 100;

}
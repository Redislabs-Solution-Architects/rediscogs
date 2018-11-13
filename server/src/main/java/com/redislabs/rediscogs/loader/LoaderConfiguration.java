package com.redislabs.rediscogs.loader;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@Data
public class LoaderConfiguration {

	private boolean skipLoad = false;
	private int batchSize = 50;
	private EntityType[] entities = { EntityType.Masters };
	private String hashArrayDelimiter = " ";
	private String fileUrlTemplate = "https://discogs-data.s3-us-west-2.amazonaws.com/data/2018/discogs_20181001_{entity}.xml.gz";

	@Bean
	public StringRedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		return template;
	}

}
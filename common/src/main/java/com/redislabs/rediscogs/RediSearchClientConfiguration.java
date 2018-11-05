package com.redislabs.rediscogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.redisearch.client.Client;
import lombok.Data;

@Configuration
@Component
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@Data
public class RediSearchClientConfiguration {

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int DEFAULT_POOLSIZE = 1;

	private String redisearchHost;
	private Integer redisearchPort;

	public String getIndexName(EntityType type) {
		return type.id() + "Idx";
	}

	public String getSuggestIndexName(EntityType type) {
		return type.id() + "SuggestIdx";
	}

	@Bean
	public StringRedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		return template;
	}

	@Autowired
	private RedisProperties redisProps;

	public Client getClient(String index) {
		return new Client(index, getHost(), getPort(), getTimeout(), getPoolSize());
	}

	private int getPort() {
		if (redisearchPort == null) {
			return redisProps.getPort();
		}
		return redisearchPort;
	}

	private String getHost() {
		if (redisearchHost == null) {
			return redisProps.getHost();
		}
		return redisearchHost;
	}

	private int getPoolSize() {
		if (redisProps.getJedis().getPool() == null) {
			return DEFAULT_POOLSIZE;
		}
		return redisProps.getJedis().getPool().getMaxActive();
	}

	private int getTimeout() {
		if (redisProps.getTimeout() == null) {
			return DEFAULT_TIMEOUT;
		}
		return (int) redisProps.getTimeout().getSeconds();
	}

	public Client getSearchClient(EntityType type) {
		return getClient(getIndexName(type));
	}

	public Client getSuggestClient(EntityType type) {
		return getClient(getSuggestIndexName(type));
	}
}

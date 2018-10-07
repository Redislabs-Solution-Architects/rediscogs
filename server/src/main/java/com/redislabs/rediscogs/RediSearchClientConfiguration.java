package com.redislabs.rediscogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;

import io.redisearch.client.Client;

@Component
public class RediSearchClientConfiguration {

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int DEFAULT_POOLSIZE = 1;

	@Autowired
	private RediscogsConfiguration config;

	@Autowired
	private RedisProperties redisProps;

	public Client getClient(String index) {
		return new Client(index, getHost(), getPort(), getTimeout(), getPoolSize());
	}

	private int getPort() {
		if (config.getRedisearchPort() == null) {
			return redisProps.getPort();
		}
		return config.getRedisearchPort();
	}

	private String getHost() {
		if (config.getRedisearchHost() == null) {
			return redisProps.getHost();
		}
		return config.getRedisearchHost();
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
}

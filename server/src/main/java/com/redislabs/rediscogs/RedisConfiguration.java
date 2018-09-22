package com.redislabs.rediscogs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.RediSearchConnection;

import io.lettuce.core.RedisURI;

@Configuration
public class RedisConfiguration {
//
//	@Bean
//	JedisConnectionFactory jedisConnectionFactory() {
//		return new JedisConnectionFactory();
//	}
//
//	@Bean
//	public StringRedisTemplate redisTemplate() {
//		StringRedisTemplate template = new StringRedisTemplate();
//		template.setConnectionFactory(jedisConnectionFactory());
//		return template;
//	}

	@Bean
	public LettuceConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory();
	}

	@Bean(destroyMethod = "shutdown")
	public RediSearchClient lettuceClient(LettuceConnectionFactory connectionFactory) {
		LettuceClientConfiguration clientConfiguration = connectionFactory.getClientConfiguration();
		RedisURI uri = createRedisURIAndApplySettings(connectionFactory.getHostName(), connectionFactory.getPort(),
				connectionFactory.getPassword(), clientConfiguration);
		RediSearchClient rediSearchClient = clientConfiguration.getClientResources() //
				.map(clientResources -> RediSearchClient.create(clientResources, uri)) //
				.orElseGet(() -> RediSearchClient.create(uri));
		clientConfiguration.getClientOptions().ifPresent(rediSearchClient::setOptions);
		return rediSearchClient;
	}

	private RedisURI createRedisURIAndApplySettings(String hostName, int port, String password,
			LettuceClientConfiguration clientConfiguration) {
		RedisURI.Builder builder = RedisURI.Builder.redis(hostName, port);
		if (password != null) {
			builder.withPassword(password);
		}
		builder.withSsl(clientConfiguration.isUseSsl());
		builder.withVerifyPeer(clientConfiguration.isVerifyPeer());
		builder.withStartTls(clientConfiguration.isStartTls());
		builder.withTimeout(clientConfiguration.getCommandTimeout());
		return builder.build();
	}

	@Bean(destroyMethod = "close")
	RediSearchConnection<String, String> connection(RediSearchClient searchClient) {
		return searchClient.connect();
	}

	@Bean
	public StringRedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		return template;
	}

}

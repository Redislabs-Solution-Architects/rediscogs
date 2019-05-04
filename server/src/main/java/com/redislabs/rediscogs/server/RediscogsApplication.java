package com.redislabs.rediscogs.server;

import org.ruaux.jdiscogs.JDiscogsConfiguration;
import org.ruaux.jdiscogs.data.BatchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.redislabs.springredisearch.RediSearchConfiguration;

@SpringBootApplication(scanBasePackageClasses = { RediscogsConfiguration.class, RediSearchConfiguration.class,
		JDiscogsConfiguration.class })
@EnableCaching
@EnableRedisRepositories
public class RediscogsApplication implements ApplicationRunner {

	@Autowired
	private BatchConfiguration batch;

	public static void main(String[] args) {
		SpringApplication.run(RediscogsApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.applyPermitDefaultValues();
		source.registerCorsConfiguration("/**", config);
		return new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		batch.runJobs();
	}

}

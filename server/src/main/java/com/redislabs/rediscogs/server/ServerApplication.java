package com.redislabs.rediscogs.server;

import java.net.MalformedURLException;

import org.ruaux.jdiscogs.JDiscogsConfiguration;
import org.ruaux.jdiscogs.data.BatchConfiguration;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
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

@SpringBootApplication(scanBasePackageClasses = { ServerConfiguration.class, RediSearchConfiguration.class,
		JDiscogsConfiguration.class })
@EnableCaching
@EnableRedisRepositories
public class ServerApplication implements ApplicationRunner {

	@Autowired
	private BatchConfiguration batch;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
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
	public void run(ApplicationArguments args) throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException, MalformedURLException {
		batch.runJobs();
	}

}

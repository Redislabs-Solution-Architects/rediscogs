package com.redislabs.rediscogs;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.redislabs.rediscogs.loader.BatchConfiguration;
import com.redislabs.rediscogs.loader.EntityType;
import com.redislabs.rediscogs.loader.LoaderConfiguration;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableCaching
@Slf4j
public class ServerApplication implements ApplicationRunner {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private BatchConfiguration batch;
	@Autowired
	private LoaderConfiguration config;

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
	public void run(ApplicationArguments args) {
		if (!config.isSkipLoad()) {
			for (EntityType entityType : config.getEntities()) {
				try {
					jobLauncher.run(batch.getLoadJob(entityType), new JobParameters());
				} catch (Exception e) {
					log.error("Could not load masters data", e);
				}
			}
		}
	}

}

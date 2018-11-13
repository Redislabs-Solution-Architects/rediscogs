package com.redislabs.rediscogs.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.redislabs.rediscogs.loader.RediSearchConfiguration;

@SpringBootApplication(scanBasePackageClasses = { RediSearchConfiguration.class, ServerConfiguration.class })
@EnableCaching
public class ServerApplication {

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

//	@Override
//	public void run(ApplicationArguments args) throws Exception {
//		if (!config.isSkipLoad()) {
//			loader.run(args);
//		}
//	}
}

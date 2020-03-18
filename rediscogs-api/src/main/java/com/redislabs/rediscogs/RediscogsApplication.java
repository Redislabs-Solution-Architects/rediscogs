package com.redislabs.rediscogs;

import org.ruaux.jdiscogs.data.JDiscogsBatchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RediscogsApplication implements ApplicationRunner {

	@Autowired
	private JDiscogsBatchConfiguration batch;
	@Autowired
	private LikeConsumer likeConsumer;

	public static void main(String[] args) {
		SpringApplication.run(RediscogsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		batch.runJobs();
		likeConsumer.start();
	}

}

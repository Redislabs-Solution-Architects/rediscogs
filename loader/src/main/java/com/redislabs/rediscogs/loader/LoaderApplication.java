package com.redislabs.rediscogs.loader;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LoaderApplication implements ApplicationRunner {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private BatchConfiguration batch;
	@Autowired
	private LoaderConfiguration config;

	public static void main(String[] args) {
		SpringApplication.run(LoaderApplication.class, args);
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

package com.redislabs.rediscogs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RediscogsApplication implements ApplicationRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job masterLoadJob;

	public static void main(String[] args) {
		SpringApplication.run(RediscogsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			jobLauncher.run(masterLoadJob, new JobParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

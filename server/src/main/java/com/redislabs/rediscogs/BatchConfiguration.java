package com.redislabs.rediscogs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ResourceUtils;

import com.redislabs.rediscogs.xml.Master;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Autowired
	private RedisItemWriter redisWriter;
	@Autowired
	private RediSearchItemWriter rediSearchWriter;
	@Autowired
	private RediscogsConfiguration config;

	@Bean
	public ItemReader<Master> reader() throws MalformedURLException {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Master.class);
		return new StaxEventItemReaderBuilder<Master>().name("masterItemReader").addFragmentRootElements("master")
				.resource(resource()).unmarshaller(marshaller).build();
	}

	private Resource resource() throws MalformedURLException {
		Resource resource = getResource(config.getMastersFile());
		if (config.getMastersFile().endsWith(".gz")) {
			try {
				return new GZIPResource(resource);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return resource;
	}

	private Resource getResource(String path) throws MalformedURLException {
		if (ResourceUtils.isUrl(path)) {
			return new UrlResource(path);
		}
		return new FileSystemResource(path);
	}

	@Bean
	public Job masterLoadJob(Step releaseLoadStep) {
		return jobBuilderFactory.get("masterLoadJob").incrementer(new RunIdIncrementer()).flow(releaseLoadStep).end()
				.build();
	}

	@Bean
	public ItemProcessor<Master, RedisMaster> processor() {
		return new MasterProcessor();
	}

	@Bean
	public Step releaseLoadStep(ItemReader<Master> reader) throws MalformedURLException {
		return stepBuilderFactory.get("masterLoadStep").<Master, RedisMaster>chunk(config.getBatchSize())
				.reader(reader()).processor(processor()).writer(writer()).listener(listener()).build();

	}

	private ItemWriter<? super RedisMaster> writer() {
		CompositeItemWriter<RedisMaster> compositeWriter = new CompositeItemWriter<>();
		compositeWriter.setDelegates(Arrays.asList(redisWriter, rediSearchWriter));
		return compositeWriter;
	}

	private WriterListener listener() {
		return new WriterListener();
	}
}

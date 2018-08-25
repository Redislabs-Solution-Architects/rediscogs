package com.redislabs.rediscogs;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private RedisItemWriter redisWriter;

	@Autowired
	private RediSearchItemWriter rediSearchWriter;

	@Value("${discogs-masters-url:discogs-masters.xml}")
	private String url;

	private int batchSize = 500;

	@Bean
	public ItemReader<Master> reader() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Master.class);
		return new StaxEventItemReaderBuilder<Master>().name("masterItemReader").addFragmentRootElements("master")
				.resource(resource()).unmarshaller(marshaller).build();
	}

	private Resource resource() {
		Resource resource = resourceLoader.getResource(url);
		if (url.endsWith(".gz")) {
			try {
				return new GZIPResource(resource);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return resource;
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
	public Step releaseLoadStep(ItemReader<Master> reader) {
		return stepBuilderFactory.get("masterLoadStep").<Master, RedisMaster>chunk(batchSize).reader(reader())
				.processor(processor()).writer(writer()).listener(listener()).build();

	}

	private ItemWriter<? super RedisMaster> writer() {
		CompositeItemWriter<RedisMaster> compositeWriter = new CompositeItemWriter<>();
		compositeWriter.setDelegates(Arrays.asList(redisWriter, rediSearchWriter));
		return compositeWriter;
	}

	private MasterChunkListener listener() {
		return new MasterChunkListener();
	}
}

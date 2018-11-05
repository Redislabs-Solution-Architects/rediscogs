package com.redislabs.rediscogs.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.util.UriTemplate;

import com.redislabs.rediscogs.EntityType;
import com.redislabs.rediscogs.loader.discogs.Master;
import com.redislabs.rediscogs.loader.discogs.Release;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Autowired
	private MasterWriter masterWriter;
	@Autowired
	private ReleaseWriter releaseWriter;
	@Autowired
	private LoaderConfiguration config;
	@Autowired
	private MasterProcessor masterProcessor;
	@Autowired
	private ReleaseProcessor releaseProcessor;

	private ItemReader<Object> getReader(EntityType type) throws MalformedURLException {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(getRootClass(type));
		return new StaxEventItemReaderBuilder<Object>().name(type.id() + "-reader")
				.addFragmentRootElements(getRootElementName(type)).resource(resource(type)).unmarshaller(marshaller)
				.build();
	}

	private Class<?> getRootClass(EntityType type) {
		switch (type) {
		case Masters:
			return Master.class;
		default:
			return Release.class;
		}
	}

	private String getRootElementName(EntityType type) {
		switch (type) {
		case Masters:
			return "master";
		case Artists:
			return "artist";
		case Labels:
			return "label";
		default:
			return "release";
		}
	}

	private Resource resource(EntityType type) throws MalformedURLException {
		UriTemplate template = new UriTemplate(config.getFileUrlTemplate());
		URI uri = template.expand(type.id());
		Resource resource = getResource(uri);
		if (uri.getPath().endsWith(".gz")) {
			try {
				return new GZIPResource(resource);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return resource;
	}

	private Resource getResource(URI uri) throws MalformedURLException {
		if (uri.isAbsolute()) {
			return new UrlResource(uri);
		}
		return new FileSystemResource(uri.toString());
	}

	public Job getLoadJob(EntityType type) throws MalformedURLException {
		TaskletStep loadStep = stepBuilderFactory.get(type.id() + "-load-step")
				.<Object, Map<String, Object>>chunk(config.getBatchSize()).reader(getReader(type))
				.processor(getProcessor(type)).writer(getWriter(type)).listener(new LoaderJobListener(type)).build();
		return jobBuilderFactory.get("masterLoadJob").incrementer(new RunIdIncrementer()).flow(loadStep).end().build();
	}

	private ItemWriter<? super Map<String, Object>> getWriter(EntityType type) {
		switch (type) {
		case Masters:
			return masterWriter;
		case Releases:
			return releaseWriter;
		default:
			return null;
		}
	}

	private ItemProcessor<Object, Map<String, Object>> getProcessor(EntityType type) {
		switch (type) {
		case Masters:
			return masterProcessor;
		case Releases:
			return releaseProcessor;
		default:
			return null;
		}
	}

}

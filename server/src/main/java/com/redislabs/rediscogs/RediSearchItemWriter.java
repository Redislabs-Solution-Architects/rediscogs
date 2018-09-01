package com.redislabs.rediscogs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.redisearch.Schema;
import io.redisearch.Suggestion;
import io.redisearch.client.Client;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

@Component
@Slf4j
public class RediSearchItemWriter extends ItemStreamSupport implements ItemWriter<RedisMaster> {

	@Autowired
	private RediSearchClientConfiguration rediSearchConfig;
	@Autowired
	private RediscogsConfiguration config;

	private Client client;
	private Client artistSuggestionClient;

	@Override
	public void open(ExecutionContext executionContext) {
		this.client = rediSearchConfig.getClient(config.getMastersIndex());
		this.artistSuggestionClient = rediSearchConfig.getClient(config.getArtistsSuggestionIdx());
		Schema schema = new Schema();
		schema.addTextField("title", 1);
		schema.addTextField("artist", 1);
		schema.addSortableNumericField("year");
		schema.addTextField("genre", 1);
		try {
			client.createIndex(schema, Client.IndexOptions.Default());
		} catch (JedisException e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not create index", e);
			} else {
				log.info("Could not create index, might already exist");
			}
		}
	}

	@Override
	public void write(List<? extends RedisMaster> items) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Writing to Redis with " + items.size() + " items.");
		}
		for (RedisMaster item : items) {
			String key = item.getId();
			try {
				client.addDocument(key, 1, getFields(item), true, false, null);
			} catch (JedisDataException e) {
				if ("Document already in index".equals(e.getMessage())) {
					log.debug(e.getMessage());
				} else {
					log.error("Could not add document: {}", e.getMessage());
				}
			}
			Suggestion suggestion = Suggestion.builder().str(item.getArtist()).build();
			artistSuggestionClient.addSuggestion(suggestion, true);
		}
	}

	private Map<String, Object> getFields(RedisMaster item) {
		Map<String, Object> fields = new LinkedHashMap<String, Object>();
		if (item.getTitle() != null) {
			fields.put("title", item.getTitle());
		}
		if (item.getArtist() != null) {
			fields.put("artist", item.getArtist());
		}
		if (item.getYear() != null) {
			fields.put("year", item.getYear());
		}
		if (item.getGenre() != null) {
			fields.put("genre", item.getGenre());
		}
		return fields;
	}

}

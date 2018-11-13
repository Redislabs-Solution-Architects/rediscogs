package com.redislabs.rediscogs.loader;

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
public class MasterWriter extends ItemStreamSupport implements ItemWriter<Map<String, Object>> {

	@Autowired
	private RediSearchConfiguration rediSearchConfig;
	private Client client;
	private Client artistSuggestionClient;

	@Override
	public void open(ExecutionContext executionContext) {
		this.client = rediSearchConfig.getSearchClient(EntityType.Masters.id());
		this.artistSuggestionClient = rediSearchConfig.getSuggestClient(EntityType.Artists.id());
		Schema schema = new Schema();
		schema.addSortableTextField("artist", 1);
		schema.addSortableTextField("artistId", 1);
		schema.addSortableTextField("dataQuality", 1);
		schema.addSortableTextField("genres", 1);
		schema.addSortableTextField("styles", 1);
		schema.addSortableTextField("title", 1);
		schema.addSortableNumericField("year");
		schema.addSortableTextField("image", 1);
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
	public void write(List<? extends Map<String, Object>> items) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Writing to Redis with " + items.size() + " items.");
		}
		for (Map<String, Object> item : items) {
			String key = EntityType.Masters.id() + ":" + (String) item.get("id");
			try {
				client.addDocument(key, 1, item, false, false, null);
			} catch (JedisDataException e) {
				if ("Document already in index".equals(e.getMessage())) {
					log.debug(e.getMessage());
				} else {
					log.error("Could not add document: {}", e.getMessage());
				}
			}
			Suggestion suggestion = Suggestion.builder().str((String) item.get("artist"))
					.payload((String) item.get("artistId")).build();
			artistSuggestionClient.addSuggestion(suggestion, true);
		}
	}

}

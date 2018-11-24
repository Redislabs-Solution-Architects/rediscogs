package com.redislabs.rediscogs.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.redisearch.Document;
import io.redisearch.Schema;
import io.redisearch.client.Client;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

@Component
@Slf4j
public class ReleaseWriter extends ItemStreamSupport implements ItemWriter<Map<String, Object>> {

	@Autowired
	private RediSearchConfiguration rediSearchConfig;
	private Client client;

	@Override
	public void open(ExecutionContext executionContext) {
		this.client = rediSearchConfig.getSearchClient(EntityType.Releases.id());
		Schema schema = new Schema();
		schema.addSortableTextField("artist", 1);
		schema.addSortableTextField("artistId", 1);
		schema.addSortableTextField("dataQuality", 1);
		schema.addSortableTextField("genres", 1);
		schema.addSortableTextField("styles", 1);
		schema.addSortableTextField("title", 1);
		schema.addSortableNumericField("released");
		schema.addSortableTextField("image", 1);
		schema.addSortableNumericField("tracks");
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
		List<Document> docs = new ArrayList<>();
		for (Map<String, Object> item : items) {
			String docId = (String) item.get("id");
			docs.add(new Document(docId, item));
		}
		boolean success = false;
		int retries = 0;
		while (!success && retries < 10) {
			try {
				client.addDocuments(docs.toArray(new Document[docs.size()]));
				success = true;
			} catch (JedisDataException e) {
				if ("Document already in index".equals(e.getMessage())) {
					log.debug(e.getMessage());
				} else {
					log.error("Could not add documents", e);
				}
			} catch (JedisConnectionException e) {
				log.error("Could not add documents", e);
				retries++;
				log.info("Sleeping before retry #{}", retries);
				Thread.sleep(5000);
			}
		}

	}

}

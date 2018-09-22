package com.redislabs.rediscogs.batch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.RediSearchConnection;
import com.redislabs.lettusearch.index.Document;
import com.redislabs.lettusearch.index.NumericField;
import com.redislabs.lettusearch.index.Schema;
import com.redislabs.lettusearch.index.Schema.SchemaBuilder;
import com.redislabs.lettusearch.index.TextField;
import com.redislabs.rediscogs.RedisMaster;
import com.redislabs.rediscogs.RediscogsConfiguration;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RediSearchItemWriter extends ItemStreamSupport implements ItemWriter<RedisMaster> {

	@Autowired
	private RediSearchConnection<String, String> connection;
	@Autowired
	private RediscogsConfiguration config;

	@Override
	public void open(ExecutionContext executionContext) {
		SchemaBuilder schema = Schema.builder();
		schema.field(TextField.builder().name("artist").build());
		schema.field(TextField.builder().name("artistId").build());
		schema.field(TextField.builder().name("dataQuality").build());
		schema.field(TextField.builder().name("genres").build());
		schema.field(NumericField.builder().name("imageWidth").sortable(true).build());
		schema.field(NumericField.builder().name("imageHeight").sortable(true).build());
		schema.field(TextField.builder().name("notes").build());
		schema.field(TextField.builder().name("styles").build());
		schema.field(TextField.builder().name("title").build());
		schema.field(NumericField.builder().name("year").sortable(true).build());
		try {
			connection.sync().create(config.getMastersIndex(), schema.build());
		} catch (Exception e) {
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
		RediSearchCommands<String, String> commands = connection.sync();
		for (RedisMaster item : items) {
			String key = item.getId();
			Map<String, String> fields = getFields(item);
			try {
				commands.add(config.getMastersIndex(), Document.builder().id(key).fields(fields).noSave(true).build());
			} catch (Exception e) {
				if ("Document already in index".equals(e.getMessage())) {
					log.debug(e.getMessage());
				} else {
					log.error("Could not add document", e);
				}
			}
			commands.suggestionAddIncrPayload(config.getArtistsSuggestionIndex(), item.getArtist(), 1,
					item.getArtistId());
		}
	}

	private Map<String, String> getFields(RedisMaster item) {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		if (item.getArtist() != null) {
			fields.put("artist", item.getArtist());
		}
		if (item.getArtist() != null) {
			fields.put("artistId", item.getArtistId());
		}
		if (item.getDataQuality() != null) {
			fields.put("dataQuality", item.getDataQuality());
		}
		if (item.getGenres() != null) {
			fields.put("genres", item.getGenres());
		}
		if (item.getStyles() != null) {
			fields.put("styles", item.getStyles());
		}
		if (item.getTitle() != null) {
			fields.put("title", item.getTitle());
		}
		if (item.getYear() != null) {
			fields.put("year", item.getYear());
		}
		return fields;
	}

}

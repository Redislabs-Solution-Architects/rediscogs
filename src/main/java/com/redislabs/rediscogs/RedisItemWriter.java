package com.redislabs.rediscogs;

import java.util.List;

import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisItemWriter extends ItemStreamSupport implements ItemWriter<RedisMaster> {

	@Autowired
	private MasterRepository repository;

	@Override
	public void write(List<? extends RedisMaster> items) throws Exception {
		repository.saveAll(items);

	}

}

package com.redislabs.rediscogs;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriterListener implements ItemWriteListener<RedisMaster> {

	private long count;

	@Override
	public void beforeWrite(List<? extends RedisMaster> items) {
	}

	@Override
	public void afterWrite(List<? extends RedisMaster> items) {
		count += items.size();
		log.info("Wrote {} entries in Redis", count);
	}

	@Override
	public void onWriteError(Exception exception, List<? extends RedisMaster> items) {

	}

}

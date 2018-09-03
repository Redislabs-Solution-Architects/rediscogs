package com.redislabs.rediscogs.batch;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import com.redislabs.rediscogs.RedisMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MasterChunkListener implements ItemWriteListener<RedisMaster> {

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

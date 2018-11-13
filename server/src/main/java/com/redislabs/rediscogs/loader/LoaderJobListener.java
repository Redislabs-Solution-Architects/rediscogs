package com.redislabs.rediscogs.loader;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoaderJobListener implements ItemWriteListener<Object> {

	private long count = 0;
	private EntityType type;

	public LoaderJobListener(EntityType type) {
		this.type = type;
	}

	@Override
	public void afterWrite(List<? extends Object> items) {
		count += items.size();
		log.info("Wrote {} {} items", count, type);
	}

	@Override
	public void beforeWrite(List<? extends Object> items) {
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Object> items) {
	}

}

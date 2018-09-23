package com.redislabs.rediscogs;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("master")
public class RedisMaster {
	private String id;
	private String title;
	private String artist;
	private String artistId;
	private String dataQuality;
	private String genres;
	private String styles;
	private String year;
}

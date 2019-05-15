package com.redislabs.rediscogs.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(of = "album")
public class Like {

	Album album;
	User user;
	String userAgent;
	Instant time;

}

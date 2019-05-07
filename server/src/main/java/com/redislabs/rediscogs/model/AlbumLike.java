package com.redislabs.rediscogs.model;

import lombok.Data;

@Data
public class AlbumLike {

	public final static String FIELD_TIME = "time";

	private String user;
	private String time;
	private Album album;

}

package com.redislabs.rediscogs.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Album {

	private String id;
	private String artist;
	private String artistId;
	private String title;
	private String year;
	private boolean like;
	private String[] genres;

}

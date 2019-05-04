package com.redislabs.rediscogs.server;

import java.util.List;

import lombok.Data;

@Data
public class Album {
	
	private String id;
	private String artist;
	private String artistId;
	private String title;
	private String year;
	private boolean favorite;
	private List<String> genres;
	private List<String> styles;

}

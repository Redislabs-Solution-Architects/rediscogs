package com.redislabs.rediscogs.model;

import java.util.List;

import lombok.Data;

@Data
public class Album {
	
	private String id;
	private String artist;
	private String artistId;
	private String title;
	private String year;
	private boolean like;
	private List<String> genres;

}

package com.redislabs.rediscogs.model;

import lombok.Data;

@Data
public class AlbumLike {
	
	private String user;
	private Album album;

}

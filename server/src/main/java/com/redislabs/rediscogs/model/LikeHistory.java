package com.redislabs.rediscogs.model;

import java.util.List;

import lombok.Data;

@Data
public class LikeHistory {
	
	List<AlbumLike> likes;

}

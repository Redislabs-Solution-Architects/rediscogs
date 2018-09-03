package com.redislabs.rediscogs;

import java.util.List;

import lombok.Data;

@Data
public class MasterEntity {

	private String id;
	private List<Image> images;

	@Data
	public static class Image {
		private String uri;
		private String uri150;
		private String type;
	}

}

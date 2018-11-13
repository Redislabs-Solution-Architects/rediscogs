package com.redislabs.rediscogs;

import java.util.List;

import lombok.Data;

@Data
public class DiscogsMaster {

	private String id;
	private List<Image> images;
	private String notes;

	@Data
	public static class Image {
		private String uri;
		private String uri150;
		private String type;
		private int height;
		private int width;
	}

}

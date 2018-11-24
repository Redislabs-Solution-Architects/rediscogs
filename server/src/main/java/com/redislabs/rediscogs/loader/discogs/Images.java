package com.redislabs.rediscogs.loader.discogs;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Images {

	@XmlElement(name = "image")
	List<Image> images;

	@Data
	@XmlRootElement(name = "image")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Image {

		@XmlAttribute(name = "height")
		private int height;
		@XmlAttribute(name = "type")
		private String type;
		@XmlAttribute(name = "uri")
		private String uri;
		@XmlAttribute(name = "uri150")
		private String uri150;
		@XmlAttribute(name = "width")
		private int width;

	}
}

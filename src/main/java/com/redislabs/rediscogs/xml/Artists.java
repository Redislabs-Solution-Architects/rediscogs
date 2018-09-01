package com.redislabs.rediscogs.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Artists {

	@XmlElement(name = "artist")
	List<Artist> artists;

	@Data
	@XmlRootElement(name = "artist")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Artist {

		@XmlElement(name = "id")
		String id;
		@XmlElement(name = "anv")
		String anv;
		@XmlElement(name = "join")
		String join;
		@XmlElement(name = "role")
		String role;
		@XmlElement(name = "name")
		String name;
		@XmlElement(name = "resource_url")
		String resource_url;
		@XmlElement(name = "tracks")
		String tracks;

	}

}
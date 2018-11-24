package com.redislabs.rediscogs.loader.discogs;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Data;

@Data
@XmlRootElement(name = "release")
@XmlAccessorType(XmlAccessType.FIELD)
public class Release {

	@XmlAttribute(name = "id")
	String id;
	@XmlAttribute(name = "status")
	String status;
	@XmlElement(name = "images")
	Images images;
	@XmlElement(name = "artists")
	Artists artists;
	@XmlElement(name = "title")
	String title;
	@XmlElement(name = "labels")
	Labels labels;
	@XmlElement(name = "extraartists")
	Artists extraArtists;
	@XmlElement(name = "formats")
	Formats formats;
	@XmlElement(name = "genres")
	Genres genres;
	@XmlElement(name = "styles")
	Styles styles;
	@XmlElement(name = "country")
	String country;
	@XmlElement(name = "released")
	String released;
	@XmlElement(name = "notes")
	String notes;
	@XmlElement(name = "data_quality")
	String dataQuality;
	@XmlElement(name = "master_id")
	MasterId masterId;
	@XmlElement(name = "tracklist")
	TrackList trackList;

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MasterId {

		@XmlValue
		String masterId;
		@XmlAttribute(name = "is_main_release")
		Boolean mainRelease;

	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Formats {

		@XmlElement(name = "format")
		List<Format> formats;

		@Data
		@XmlRootElement(name = "format")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Format {

			@XmlAttribute(name = "name")
			String name;
			@XmlAttribute(name = "qty")
			Integer qty;

		}

	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Labels {

		@XmlElement(name = "label")
		List<Label> labels;

		@Data
		@XmlRootElement(name = "label")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Label {

			@XmlAttribute(name = "id")
			String id;
			@XmlAttribute(name = "catno")
			String catno;
			@XmlAttribute(name = "name")
			String name;

		}
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TrackList {

		@XmlElement(name = "track")
		List<Track> tracks;

		@Data
		@XmlRootElement(name = "track")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Track {

			@XmlElement(name = "position")
			String position;
			@XmlElement(name = "title")
			String title;
			@XmlElement(name = "duration")
			String duration;
			@XmlElement(name = "type_")
			String type;

		}
	}

}

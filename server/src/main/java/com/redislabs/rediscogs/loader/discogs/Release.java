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
	private String id;
	@XmlAttribute(name = "status")
	private String status;
	@XmlElement(name = "images")
	private Images images;
	@XmlElement(name = "artists")
	private Artists artists;
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "labels")
	private Labels labels;
	@XmlElement(name = "extraartists")
	private Artists extraArtists;
	@XmlElement(name = "formats")
	private Formats formats;
	@XmlElement(name = "genres")
	private Genres genres;
	@XmlElement(name = "styles")
	private Styles styles;
	@XmlElement(name = "country")
	private String country;
	@XmlElement(name = "released")
	private String released;
	@XmlElement(name = "notes")
	private String notes;
	@XmlElement(name = "data_quality")
	private String dataQuality;
	@XmlElement(name = "master_id")
	private MasterId masterId;
	@XmlElement(name = "tracklist")
	private TrackList trackList;
	@XmlElement(name = "identifiers")
	private Identifiers identifiers;
	@XmlElement(name = "companies")
	private Companies companies;

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MasterId {

		@XmlValue
		private String masterId;
		@XmlAttribute(name = "is_main_release")
		private Boolean mainRelease;

	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Formats {

		@XmlElement(name = "format")
		private List<Format> formats;

		@Data
		@XmlRootElement(name = "format")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Format {

			@XmlAttribute(name = "name")
			private String name;
			@XmlAttribute(name = "qty")
			private Integer qty;
			@XmlAttribute(name = "text")
			private String text;
			@XmlElement(name = "descriptions")
			private Descriptions descriptions;

		}

	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Descriptions {
		@XmlElement(name = "description")
		private List<String> descriptions;
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Labels {

		@XmlElement(name = "label")
		private List<Label> labels;

		@Data
		@XmlRootElement(name = "label")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Label {

			@XmlAttribute(name = "id")
			private String id;
			@XmlAttribute(name = "catno")
			private String catno;
			@XmlAttribute(name = "name")
			private String name;

		}
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TrackList {

		@XmlElement(name = "track")
		private List<Track> tracks;

		@Data
		@XmlRootElement(name = "track")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Track {

			@XmlElement(name = "position")
			private String position;
			@XmlElement(name = "title")
			private String title;
			@XmlElement(name = "duration")
			private String duration;
			@XmlElement(name = "extraartists")
			private Artists extraartists;

		}
	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Identifiers {

		@XmlElement(name = "identifier")
		private List<Identifier> identifiers;

		@Data
		@XmlRootElement(name = "identifier")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Identifier {

			@XmlAttribute(name = "description")
			private String description;
			@XmlAttribute(name = "type")
			private String type;
			@XmlAttribute(name = "value")
			private String value;

		}

	}

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Companies {

		@XmlElement(name = "company")
		private List<Company> companies;

		@Data
		@XmlRootElement(name = "company")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Company {

			@XmlElement(name = "id")
			private String id;
			@XmlElement(name = "name")
			private String name;
			@XmlElement(name = "catno")
			private String catno;
			@XmlElement(name = "entity_type")
			private String entityType;
			@XmlElement(name = "entity_type_name")
			private String entityTypeName;
			@XmlElement(name = "resource_url")
			private String resourceUrl;

		}

	}

}

package com.redislabs.rediscogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "master")
public class Master {

	private String id;
	private Images images;
	private Artists artists;
	private Genres genres;
	private Styles styles;
	private String year;
	private String title;

	@XmlElement(name = "year")
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "images")
	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	@XmlElement(name = "artists")
	public Artists getArtists() {
		return artists;
	}

	public void setArtists(Artists artists) {
		this.artists = artists;
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(name = "genres")
	public Genres getGenres() {
		return genres;
	}

	public void setGenres(Genres genres) {
		this.genres = genres;
	}

	@XmlElement(name = "styles")
	public Styles getStyles() {
		return styles;
	}

	public void setStyles(Styles styles) {
		this.styles = styles;
	}
	
	public static class Styles {

		@XmlElement(name = "style")
		private List<String> styles;

		public List<String> getStyles() {
			if (styles == null) {
				styles = new ArrayList<String>();
			}
			return this.styles;
		}

		public String toString() {
			return "Styles [styles=" + Arrays.toString(styles.toArray()) + "]";
		}

	}


	@Override
	public String toString() {
		return "Release [id=" + id + ", images=" + images + ", artists=" + artists + ", title=" + title + ", genres="
				+ genres + ", styles=" + styles + "]";
	}

	public static class Genres {

		@XmlElement(name = "genre")
		private List<String> genres;

		public List<String> getGenres() {
			if (genres == null) {
				genres = new ArrayList<String>();
			}
			return this.genres;
		}

		@Override
		public String toString() {
			return "Genres [genres=" + Arrays.toString(genres.toArray()) + "]";
		}

	}

	public static class Images {

		@XmlElement(name = "image")
		private List<Image> images;

		public List<Image> getImages() {
			if (images == null) {
				images = new ArrayList<Image>();
			}
			return this.images;
		}

		@Override
		public String toString() {
			return "Images [images=" + Arrays.toString(images.toArray()) + "]";
		}

	}

	@XmlRootElement(name = "image")
	public class Image {

		private int height;
		private String type;
		private String uri;
		private String uri150;
		private int width;

		@XmlAttribute(name = "height")
		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		@XmlAttribute(name = "type")
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@XmlAttribute(name = "uri")
		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		@XmlAttribute(name = "uri150")
		public String getUri150() {
			return uri150;
		}

		public void setUri150(String uri150) {
			this.uri150 = uri150;
		}

		@XmlAttribute(name = "width")
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@Override
		public String toString() {
			return "Image [height=" + height + ", type=" + type + ", uri=" + uri + ", uri150=" + uri150 + ", width="
					+ width + "]";
		}

	}

	public static class Artists {

		@XmlElement(name = "artist")
		private List<Artist> artists;

		public List<Artist> getArtists() {
			if (artists == null) {
				artists = new ArrayList<Artist>();
			}
			return this.artists;
		}

		@Override
		public String toString() {
			return "Artists [artists=" + Arrays.toString(artists.toArray()) + "]";
		}

	}

	@XmlRootElement(name = "artist")
	public static class Artist {

		private String id;
		private String anv;
		private String join;
		private String role;
		private String name;
		private String resource_url;
		private String tracks;

		@XmlElement(name = "id")
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@XmlElement(name = "name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlElement(name = "anv")
		public String getAnv() {
			return anv;
		}

		public void setAnv(String anv) {
			this.anv = anv;
		}

		@XmlElement(name = "join")
		public String getJoin() {
			return join;
		}

		public void setJoin(String join) {
			this.join = join;
		}

		@XmlElement(name = "role")
		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		@XmlElement(name = "resource_url")
		public String getResource_url() {
			return resource_url;
		}

		public void setResource_url(String resource_url) {
			this.resource_url = resource_url;
		}

		@XmlElement(name = "tracks")
		public String getTracks() {
			return tracks;
		}

		public void setTracks(String tracks) {
			this.tracks = tracks;
		}

		@Override
		public String toString() {
			return "Artist [id=" + id + ", anv=" + anv + ", join=" + join + ", role=" + role + ", name=" + name
					+ ", resource_url=" + resource_url + ", tracks=" + tracks + "]";
		}

	}
}

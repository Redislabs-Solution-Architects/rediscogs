package com.redislabs.rediscogs.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "master")
@XmlAccessorType(XmlAccessType.FIELD)
public class Master {

	@XmlAttribute(name = "id")
	String id;
	@XmlElement(name = "artists")
	Artists artists;
	@XmlElement(name = "genres")
	Genres genres;
	@XmlElement(name = "styles")
	Styles styles;
	@XmlElement(name = "year")
	String year;
	@XmlElement(name = "title")
	String title;

}

package com.redislabs.rediscogs.batch;

import org.springframework.batch.item.ItemProcessor;

import com.redislabs.rediscogs.RedisMaster;
import com.redislabs.rediscogs.discogs.xml.Artists.Artist;
import com.redislabs.rediscogs.discogs.xml.Master;

public class MasterProcessor implements ItemProcessor<Master, RedisMaster> {

	private static final String DELIMITER = " ";

	@Override
	public RedisMaster process(Master xml) throws Exception {
		RedisMaster master = new RedisMaster();
		master.setId(xml.getId());
		if (xml.getArtists() != null) {
			if (xml.getArtists().getArtists() != null && xml.getArtists().getArtists().size() > 0) {
				Artist artist = xml.getArtists().getArtists().get(0);
				master.setArtist(artist.getName());
				master.setArtistId(artist.getId());
			}
		}
		if (xml.getDataQuality() != null) {
			master.setDataQuality(xml.getDataQuality());
		}
		if (xml.getGenres() != null && xml.getGenres().getGenres().size() > 0) {
			master.setGenres(String.join(DELIMITER, xml.getGenres().getGenres()));
		}
		if (xml.getNotes() != null) {
			master.setNotes(xml.getNotes());
		}
		if (xml.getStyles() != null && xml.getStyles().getStyles().size() > 0) {
			master.setStyles(String.join(DELIMITER, xml.getStyles().getStyles()));
		}
		master.setTitle(xml.getTitle());
		if (xml.getYear() != null && xml.getYear().length() == 4) {
			master.setYear(xml.getYear());
		}
		return master;
	}

}

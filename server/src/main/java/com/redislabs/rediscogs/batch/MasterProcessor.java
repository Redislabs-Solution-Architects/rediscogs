package com.redislabs.rediscogs.batch;

import org.springframework.batch.item.ItemProcessor;

import com.redislabs.rediscogs.RedisMaster;
import com.redislabs.rediscogs.batch.xml.Artists.Artist;
import com.redislabs.rediscogs.batch.xml.Master;

public class MasterProcessor implements ItemProcessor<Master, RedisMaster> {

	@Override
	public RedisMaster process(Master xml) throws Exception {
		RedisMaster master = new RedisMaster();
		master.setId(xml.getId());
		master.setTitle(xml.getTitle());
		if (xml.getArtists() != null) {
			if (xml.getArtists().getArtists() != null && xml.getArtists().getArtists().size() > 0) {
				Artist artist = xml.getArtists().getArtists().get(0);
				master.setArtist(artist.getName());
				master.setArtistId(artist.getId());
			}
		}
		if (xml.getGenres() != null && xml.getGenres().getGenres().size() > 0) {
			master.setGenre(xml.getGenres().getGenres().get(0));
		} else {
			if (xml.getStyles() != null && xml.getStyles().getStyles().size() > 0) {
				master.setGenre(xml.getStyles().getStyles().get(0));
			}
		}
		if (xml.getYear() != null && xml.getYear().length() == 4) {
			master.setYear(Integer.parseInt(xml.getYear()));
		}
		return master;
	}

}

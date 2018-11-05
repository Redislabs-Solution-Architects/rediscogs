package com.redislabs.rediscogs.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redislabs.rediscogs.loader.discogs.Release;
import com.redislabs.rediscogs.loader.discogs.Artists.Artist;
import com.redislabs.rediscogs.loader.discogs.Release.TrackList.Track;

@Component
public class ReleaseProcessor implements ItemProcessor<Object, Map<String, Object>> {

	@Autowired
	private LoaderConfiguration config;

	@Override
	public Map<String, Object> process(Object object) throws Exception {
		Release xml = (Release) object;
		Map<String, Object> doc = new HashMap<>();
		doc.put("id", xml.getId());
		if (xml.getArtists() != null) {
			if (xml.getArtists().getArtists() != null && xml.getArtists().getArtists().size() > 0) {
				Artist artist = xml.getArtists().getArtists().get(0);
				doc.put("artist", artist.getName());
				doc.put("artistId", artist.getId());
			}
		}
		if (xml.getDataQuality() != null) {
			doc.put("dataQuality", xml.getDataQuality());
		}
		if (xml.getGenres() != null && xml.getGenres().getGenres().size() > 0) {
			doc.put("genres", String.join(config.getHashArrayDelimiter(), xml.getGenres().getGenres()));
		}
		if (xml.getStyles() != null && xml.getStyles().getStyles().size() > 0) {
			doc.put("styles", String.join(config.getHashArrayDelimiter(), xml.getStyles().getStyles()));
		}
		doc.put("title", xml.getTitle());
		if (xml.getReleased() != null && xml.getReleased().length() == 4) {
			doc.put("released", xml.getReleased());
		}
		if (xml.getImages() != null) {
			doc.put("image", xml.getImages().getImages().size() > 0);
		}
		if (xml.getTrackList() != null) {
			if (xml.getTrackList().getTracks() != null) {
				List<Track> tracks = xml.getTrackList().getTracks();
				doc.put("tracks", tracks.size());
				for (int index = 0; index < tracks.size(); index++) {
					Track track = tracks.get(index);
					int trackNumber = index + 1;
					doc.put("track" + trackNumber, track.getTitle());
				}
			}
		}
		return doc;
	}

}

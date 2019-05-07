package com.redislabs.rediscogs;

import java.util.Arrays;
import java.util.Map;

import org.ruaux.jdiscogs.JDiscogsConfiguration;
import org.ruaux.jdiscogs.data.MasterIndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redislabs.rediscogs.model.Album;
import com.redislabs.rediscogs.model.AlbumLike;

import io.lettuce.core.StreamMessage;

@Component
public class AlbumMarshaller {

	@Autowired
	private RediscogsConfiguration config;
	@Autowired
	private JDiscogsConfiguration discogs;

	public AlbumLike toLike(StreamMessage<String, String> msg) {
		Map<String, String> fields = msg.getBody();
		AlbumLike like = new AlbumLike();
		like.setUser(fields.get(config.getUserAttribute()));
		like.setTime(fields.get(AlbumLike.FIELD_TIME));
		Album album = new Album();
		album.setId(fields.get(MasterIndexWriter.FIELD_ID));
		album.setArtist(fields.get(MasterIndexWriter.FIELD_ARTIST));
		album.setArtistId(fields.get(MasterIndexWriter.FIELD_ARTISTID));
		album.setTitle(fields.get(MasterIndexWriter.FIELD_TITLE));
		album.setYear(fields.get(MasterIndexWriter.FIELD_YEAR));
		album.setGenres(Arrays.asList(
				fields.getOrDefault(MasterIndexWriter.FIELD_GENRES, "").split(discogs.getHashArrayDelimiter())));
		like.setAlbum(album);
		return like;
	}
}

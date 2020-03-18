package com.redislabs.rediscogs;

import java.util.List;
import java.util.stream.Stream;

import org.ruaux.jdiscogs.data.JDiscogsBatchProperties;
import org.ruaux.jdiscogs.data.MasterIndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.lettusearch.search.Direction;
import com.redislabs.lettusearch.search.Limit;
import com.redislabs.lettusearch.search.SearchOptions;
import com.redislabs.lettusearch.search.SearchResults;
import com.redislabs.lettusearch.search.SortBy;
import com.redislabs.lettusearch.suggest.SuggestGetOptions;
import com.redislabs.lettusearch.suggest.SuggestResult;
import com.redislabs.rediscogs.RediscogsController.ArtistSuggestion;
import com.redislabs.rediscogs.model.Album;

@Component
public class SearchService {

	@Autowired
	private RediscogsProperties config;
	@Autowired
	private JDiscogsBatchProperties props;
	@Autowired
	private StatefulRediSearchConnection<String, String> connection;

	public Stream<ArtistSuggestion> suggestArtists(String prefix) {
		List<SuggestResult<String>> results = connection.sync().sugget(props.getArtistSuggestionIndex(), prefix,
				SuggestGetOptions.builder().withPayloads(true).max(20l).fuzzy(config.isFuzzySuggest()).build());
		return results.stream().map(s -> ArtistSuggestion.builder().name(s.string()).id(s.payload()).build());
	}

	public Stream<Album> searchAlbums(String query) {
		SearchResults<String, String> results = connection.sync().search(props.getMasterIndex(), query,
				SearchOptions.builder().limit(Limit.builder().num(config.getSearchResultsLimit()).build())
						.sortBy(SortBy.builder().field("year").direction(Direction.Ascending).build()).build());
		return results.stream().map(r -> {
			String[] genres = r.getOrDefault(MasterIndexWriter.FIELD_GENRES, "").split(props.getHashArrayDelimiter());
			return Album.builder().id(r.documentId()).artist(r.get(MasterIndexWriter.FIELD_ARTIST))
					.artistId(r.get(MasterIndexWriter.FIELD_ARTISTID)).title(r.get(MasterIndexWriter.FIELD_TITLE))
					.year(r.get(MasterIndexWriter.FIELD_YEAR)).genres(genres).build();
		});
	}

}

package com.redislabs.rediscogs.loader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;

import com.redislabs.rediscogs.loader.discogs.Release;
import com.redislabs.rediscogs.loader.discogs.Release.TrackList.Track;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XmlLoaderTests {

	@Test
	public void testReleases() throws UnexpectedInputException, ParseException, Exception {
		ClassPathResource resource = new ClassPathResource("release-4210378.xml");
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Release.class);
		StaxEventItemReader<Release> reader = new StaxEventItemReaderBuilder<Release>().name("release-reader")
				.addFragmentRootElements("release").resource(resource).unmarshaller(marshaller).build();
		ExecutionContext executionContext = new ExecutionContext();
		reader.open(executionContext);
		Release release = reader.read();
		reader.close();
		assertEquals(14, release.getTrackList().getTracks().size());
		Track bonusHeading = release.getTrackList().getTracks().get(10);
		assertEquals("Bonus Tracks", bonusHeading.getTitle());
		assertEquals("", bonusHeading.getPosition());
		assertEquals("", bonusHeading.getDuration());
	}

}

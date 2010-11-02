package org.vaadin.devoxx2k10.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.http.HttpClient;
import org.vaadin.devoxx2k10.data.http.OfflineHttpClientMock;

public class TestRestApiFacade {

    private final HttpClient jsonProvider;
    private final CachingRestApiFacade devoxxFacade;

    public TestRestApiFacade() {
        jsonProvider = new OfflineHttpClientMock();
        devoxxFacade = new CachingRestApiFacade(jsonProvider);
    }

    @Test
    public void testFullSchedule() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        Assert.assertEquals(164, schedule.size());
    }

    @Test
    public void testSpeaker() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        final DevoxxPresentation event = schedule.get(2);
        final DevoxxSpeaker speaker = event.getSpeakers().get(0);
        Assert.assertEquals(56, speaker.getId());
        Assert.assertEquals("http://cfp.devoxx.com/static/images/56/thumbnail.gif", speaker.getImageUri());
        Assert.assertEquals("Martijn Dashorst is a senior software engineer", speaker.getBio().substring(0, 46));
    }

    @Test
    public void testPresentationDetails() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        final DevoxxPresentation event = schedule.get(2);
        Assert.assertEquals("SENIOR", event.getExperience());
        Assert.assertEquals("During this keynote you'll get an overview", event.getSummary().substring(0, 42));
        Assert.assertEquals(5, event.getTags().size());
        Assert.assertTrue(event.getTags().contains("javase"));
        Assert.assertTrue(event.getTags().contains("Java7"));
        Assert.assertTrue(event.getTags().contains("Java8"));
        Assert.assertTrue(event.getTags().contains("modules"));
        Assert.assertTrue(event.getTags().contains("closures"));
    }

    @Test
    public void testPresentationSearch() {
        final List<DevoxxPresentation> result = devoxxFacade.search("Java7");
        Assert.assertEquals(2, result.size());
    }
}

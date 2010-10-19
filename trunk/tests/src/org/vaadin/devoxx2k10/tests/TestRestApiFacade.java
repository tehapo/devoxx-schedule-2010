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

    private HttpClient jsonProvider;
    private CachingRestApiFacade devoxxFacade;

    public TestRestApiFacade() {
        jsonProvider = new OfflineHttpClientMock();
        devoxxFacade = new CachingRestApiFacade(jsonProvider);
    }

    @Test
    public void testFullSchedule() {
        List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        Assert.assertEquals(164, schedule.size());
    }

    @Test
    public void testSpeaker() {
        List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        DevoxxPresentation event = schedule.get(2);
        DevoxxSpeaker speaker = event.getSpeakers().get(0);
        Assert.assertEquals(56, speaker.getId());
        Assert.assertEquals(
                "http://cfp.devoxx.com/static/images/56/thumbnail.gif",
                speaker.getImageUri());
        Assert.assertEquals("Martijn Dashorst is a senior software engineer",
                speaker.getBio().substring(0, 46));
    }

    @Test
    public void testPresentationDetails() {
        List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        DevoxxPresentation event = schedule.get(2);
        Assert.assertEquals("SENIOR", event.getExperience());
        Assert.assertEquals(
                "Aspect-Oriented Programming (AOP) complements Object-Oriented Programming (OOP)",
                event.getSummary().substring(0, 79));
    }
}

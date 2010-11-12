package org.vaadin.devoxx2k10.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.http.HttpClient;
import org.vaadin.devoxx2k10.data.http.impl.OfflineHttpClientMock;

public class TestRestApiFacade {

    private final HttpClient httpClient;
    private final CachingRestApiFacade devoxxFacade;

    public TestRestApiFacade() {
        httpClient = new OfflineHttpClientMock("20101112110640");
        devoxxFacade = new CachingRestApiFacade(httpClient);
    }

    @Test
    public void testFullSchedule() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        Assert.assertEquals(174, schedule.size());
    }

    @Test
    public void testSpeaker() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        final DevoxxPresentation event = schedule.get(2);
        final DevoxxSpeaker speaker = event.getSpeakers().get(0);
        Assert.assertEquals(83, speaker.getId());
        Assert.assertEquals("Bruno Lowagie", speaker.getName());
        Assert.assertEquals("http://cfp.devoxx.com/static/images/83/thumbnail.gif", speaker.getImageUri());
        Assert.assertEquals("Bruno Lowagie is the original developer", speaker.getBio().substring(0, 39));
    }

    @Test
    public void testPresentationDetails() {
        final List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        final DevoxxPresentation event = schedule.get(2);
        Assert.assertEquals(60, event.getId());
        Assert.assertEquals("SENIOR", event.getExperience());
        Assert.assertEquals("This year's Conference Guide", event.getSummary().substring(0, 28));
        Assert.assertEquals(3, event.getTags().size());
        Assert.assertTrue(event.getTags().contains("adobe"));
        Assert.assertTrue(event.getTags().contains("XML"));
        Assert.assertTrue(event.getTags().contains("PDF"));
    }

}

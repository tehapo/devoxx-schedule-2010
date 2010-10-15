package org.vaadin.devoxx2k10.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.json.JsonDataProvider;
import org.vaadin.devoxx2k10.data.json.OfflineJsonDataProvider;

public class TestRestApiFacade {

    private JsonDataProvider jsonProvider;
    private CachingRestApiFacade devoxxFacade;

    private static final Date TUE_START;
    private static final Date TUE_END;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 10, 16, 0, 0);
        TUE_START = cal.getTime();

        cal.set(2010, 10, 16, 23, 59);
        TUE_END = cal.getTime();
    }

    public TestRestApiFacade() {
        jsonProvider = new OfflineJsonDataProvider();
        devoxxFacade = new CachingRestApiFacade(jsonProvider);
    }

    @Test
    public void testFullSchedule() {
        List<DevoxxPresentation> schedule = devoxxFacade.getFullSchedule();
        Assert.assertEquals(164, schedule.size());
    }

    @Test
    public void testTuesdaySchedule() {
        List<DevoxxPresentation> schedule = devoxxFacade.getSchedule(TUE_START,
                TUE_END);
        Assert.assertEquals(31, schedule.size());
    }

    @Test
    public void testSpeaker() {
        List<DevoxxPresentation> schedule = devoxxFacade.getSchedule(TUE_START,
                TUE_END);
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
        List<DevoxxPresentation> schedule = devoxxFacade.getSchedule(TUE_START,
                TUE_END);
        DevoxxPresentation event = schedule.get(2);
        Assert.assertEquals("SENIOR", event.getExperience());
        Assert.assertEquals(
                "Aspect-Oriented Programming (AOP) complements Object-Oriented Programming (OOP)",
                event.getSummary().substring(0, 79));
    }
}

package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;

public class DevoxxEventProvider extends BasicEventProvider {

    private static final long serialVersionUID = -6066313242075569496L;

    private transient final Logger logger = Logger.getLogger(getClass());
    private transient final RestApiFacade facade = new CachingRestApiFacade();
    private boolean eventsLoaded;

    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        if (!eventsLoaded) {
            loadEventsFromBackend();
            eventsLoaded = true;
        }

        List<CalendarEvent> result = super.getEvents(startDate, endDate);
        logger.debug("Returning " + result.size() + " events for " + startDate
                + " - " + endDate);
        return result;
    }

    private void loadEventsFromBackend() {
        List<DevoxxPresentation> schedule = facade.getFullSchedule();

        // wrap data from the model into CalendarEvents for UI
        for (DevoxxPresentation event : schedule) {
            String caption = event.getTitle() + " " + getSpeakersString(event);
            DevoxxCalendarEvent calEvent = new DevoxxCalendarEvent();
            calEvent.setStart(event.getFromTime());
            calEvent.setEnd(event.getToTime());
            calEvent.setCaption(caption);
            calEvent.setDescription(caption);
            calEvent.setStyleName(event.getKind().name().toLowerCase());
            if (calEvent.getStyleName().equals("registration")) {
                calEvent.addStyleName("java");
            }
            calEvent.setDevoxxEvent(event);
            calEvent.addListener(this);
            super.addEvent(calEvent);
        }

        logger.debug("Fetched schedule from backend (total " + schedule.size()
                + " events).");
    }

    private String getSpeakersString(DevoxxPresentation event) {
        StringBuilder speakers = new StringBuilder();
        if (!event.getSpeakers().isEmpty()) {
            speakers.append('(');
            for (DevoxxSpeaker speaker : event.getSpeakers()) {
                if (speakers.length() > 1) {
                    speakers.append(", ");
                }
                speakers.append(speaker.getName());
            }
            speakers.append(')');
        }
        return speakers.toString();
    }

}

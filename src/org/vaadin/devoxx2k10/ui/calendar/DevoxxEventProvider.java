package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;

public class DevoxxEventProvider extends BasicEventProvider {

    private static final long serialVersionUID = -6066313242075569496L;

    private transient final Logger logger = Logger.getLogger(getClass());
    private boolean eventsLoaded;

    private static final long SHORT_EVENT_THRESHOLD_MS = 1000 * 60 * 30;

    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        if (!eventsLoaded) {
            loadEventsFromBackend();
            eventsLoaded = true;
        }

        List<CalendarEvent> result = super.getEvents(startDate, endDate);
        logger.debug("Returning " + result.size() + " events for " + startDate
                + " - " + endDate);

        assignAttendingStyles(result);

        return result;
    }

    private void assignAttendingStyles(List<CalendarEvent> events) {
        MyScheduleUser user = (MyScheduleUser) DevoxxScheduleApplication
                .getCurrentInstance().getUser();
        if (user != null && user.getFavourites() != null) {
            for (CalendarEvent event : events) {
                if (event instanceof DevoxxCalendarEvent) {
                    DevoxxCalendarEvent devoxxEvent = (DevoxxCalendarEvent) event;
                    if (user.getFavourites().contains(
                            devoxxEvent.getDevoxxEvent().getId())) {
                        devoxxEvent.addStyleName("attending");
                    }
                }
            }
        }
    }

    private void loadEventsFromBackend() {
        RestApiFacade facade = DevoxxScheduleApplication.getCurrentInstance()
                .getBackendFacade();
        List<DevoxxPresentation> schedule = facade.getFullSchedule();

        // wrap data from the model into CalendarEvents for UI
        for (DevoxxPresentation event : schedule) {
            String caption = event.getTitle() + " " + getSpeakersString(event);
            DevoxxCalendarEvent calEvent = new DevoxxCalendarEvent();
            calEvent.setStart(event.getFromTime());
            calEvent.setEnd(event.getToTime());
            calEvent.setCaption(caption);
            calEvent.setStyleName(event.getKind().name().toLowerCase());
            calEvent.addStyleName("at-"
                    + event.getRoom().toLowerCase().replaceAll(" ", ""));
            if (isShortEvent(event)) {
                calEvent.addStyleName("short-event");
            }
            calEvent.setDevoxxEvent(event);
            calEvent.addListener(this);
            super.addEvent(calEvent);
        }

        logger.debug("Fetched schedule from backend (total " + schedule.size()
                + " events).");
    }

    private static boolean isShortEvent(DevoxxPresentation event) {
        return event.getToTime().getTime() - event.getFromTime().getTime() < SHORT_EVENT_THRESHOLD_MS;
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

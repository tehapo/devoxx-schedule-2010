package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;

public class DevoxxEventProvider extends BasicEventProvider {

    private static final long serialVersionUID = -6066313242075569496L;

    private transient final Logger logger = Logger.getLogger(getClass());
    private boolean eventsLoaded;

    private static final long SHORT_EVENT_THRESHOLD_MS = 1000 * 60 * 30;

    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        loadEventsFromBackendIfNeeded();

        List<CalendarEvent> result = super.getEvents(startDate, endDate);
        logger.debug("Returning " + result.size() + " events for " + startDate
                + " - " + endDate);

        return result;
    }

    public CalendarEvent getEvent(int id) {
        loadEventsFromBackendIfNeeded();

        for (CalendarEvent event : eventList) {
            if (event instanceof DevoxxCalendarEvent
                    && ((DevoxxCalendarEvent) event).getDevoxxEvent().getId() == id) {
                return event;
            }
        }

        return null;
    }

    public void refreshAttendingStyles() {
        MyScheduleUser user = (MyScheduleUser) DevoxxScheduleApplication
                .getCurrentInstance().getUser();
        for (CalendarEvent event : eventList) {
            if (event instanceof DevoxxCalendarEvent) {
                DevoxxCalendarEvent devoxxEvent = (DevoxxCalendarEvent) event;
                if (user != null
                        && user.hasFavourited(devoxxEvent.getDevoxxEvent())) {
                    devoxxEvent.addStyleName("attending");
                } else {
                    devoxxEvent.removeStyleName("attending");
                }
            }
        }
    }

    private void loadEventsFromBackendIfNeeded() {
        if (eventsLoaded) {
            // already loaded -> do nothing
            return;
        }

        RestApiFacade facade = DevoxxScheduleApplication.getCurrentInstance()
                .getBackendFacade();
        List<DevoxxPresentation> schedule = facade.getFullSchedule();

        // wrap data from the model into CalendarEvents for UI
        for (DevoxxPresentation event : schedule) {
            DevoxxCalendarEvent calEvent = new DevoxxCalendarEvent();
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
        eventsLoaded = true;

        logger.debug("Fetched schedule from backend (total " + schedule.size()
                + " events).");
    }

    private static boolean isShortEvent(DevoxxPresentation event) {
        return event.getToTime().getTime() - event.getFromTime().getTime() < SHORT_EVENT_THRESHOLD_MS;
    }

}

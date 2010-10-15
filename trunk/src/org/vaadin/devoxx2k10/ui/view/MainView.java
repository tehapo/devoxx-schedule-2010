package org.vaadin.devoxx2k10.ui.view;

import java.util.Date;

import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendar;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;
import org.vaadin.devoxx2k10.ui.view.NavigationPanel.DateChangeListener;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

/**
 * The main view of the application displaying navigation, calendar and details.
 */
public class MainView extends HorizontalLayout implements EventClickHandler,
        DateChangeListener {

    private static final long serialVersionUID = 7622207451668068454L;

    private DevoxxCalendar calendar;
    private DaySelector daySelector;
    private DevoxxCalendarEvent selectedEvent;
    private EventDetailsPanel detailsPanel;
    private NavigationPanel navigationPanel;

    public MainView() {
        initUi();
    }

    private void initUi() {
        setWidth("100%");
        setHeight("100%");

        calendar = new DevoxxCalendar();
        calendar.setHandler(this);
        calendar.setDate(DevoxxCalendar.getDefaultDate());

        // addComponent(constructNavigation());
        navigationPanel = new NavigationPanel(DevoxxCalendar.getDefaultDate());
        navigationPanel.addListener(this);
        addComponent(navigationPanel);

        Panel calendarPanel = new Panel();
        calendarPanel.setStyleName("calendar-panel");
        calendarPanel.setSizeFull();
        calendarPanel.addComponent(calendar);
        addComponent(calendarPanel);

        detailsPanel = new EventDetailsPanel();
        detailsPanel.setVisible(false); // hide at first
        addComponent(detailsPanel);

        // make the calendar expand to use all available space
        setExpandRatio(calendarPanel, 1f);
    }

    public void dateChanged(Date newDate) {
        calendar.setDate(newDate);
    }

    public void eventClick(EventClick event) {
        CalendarEvent calEvent = event.getCalendarEvent();

        if (calEvent instanceof DevoxxCalendarEvent) {
            detailsPanel.setVisible(true);

            if (selectedEvent != null) {
                selectedEvent.removeStyleName("selected");
            }

            DevoxxCalendarEvent devoxxCalEvent = (DevoxxCalendarEvent) calEvent;
            selectedEvent = devoxxCalEvent;
            devoxxCalEvent.addStyleName("selected");

            detailsPanel.setEvent(devoxxCalEvent.getDevoxxEvent());
        }
    }
}

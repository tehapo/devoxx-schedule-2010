package org.vaadin.devoxx2k10.ui.view;

import java.util.Date;

import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendar;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;
import org.vaadin.devoxx2k10.ui.view.NavigationPanel.DateChangeListener;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;

/**
 * The main view of the application displaying navigation, calendar and details.
 */
public class MainView extends HorizontalLayout implements EventClickHandler,
        DateChangeListener, ClickListener, ValueChangeListener {

    private static final long serialVersionUID = 7622207451668068454L;

    private DevoxxCalendar calendar;
    private DevoxxCalendarEvent selectedEvent;
    private EventDetailsPanel detailsPanel;
    private NavigationPanel navigationPanel;
    private CheckBox calendarMode;
    private DaySelectorField daySelector;

    public MainView() {
        initUi();
    }

    private void initUi() {
        setWidth("100%");
        setHeight("100%");

        calendar = new DevoxxCalendar();
        calendar.setHandler(this);
        calendar.setDate(DevoxxCalendar.getDefaultDate());

        /*-navigationPanel = new NavigationPanel(DevoxxCalendar.getDefaultDate());
        navigationPanel.addListener(this);
        addComponent(navigationPanel);*/

        calendarMode = new CheckBox("Highlight events I'm attending", this);
        calendarMode.setImmediate(true);

        daySelector = new DaySelectorField(DevoxxCalendar.DEVOXX_FIRST_DAY,
                DevoxxCalendar.DEVOXX_LAST_DAY);
        daySelector.addListener((ValueChangeListener) this);
        daySelector.setValue(DevoxxCalendar.getDefaultDate());

        CssLayout toolbar = new CssLayout();
        toolbar.setWidth("100%");
        toolbar.setHeight("");
        toolbar.setStyleName("toolbar");
        toolbar.addComponent(daySelector);
        toolbar.addComponent(calendarMode);

        CssLayout calendarWrapper = new CssLayout();
        calendarWrapper.setMargin(true);
        calendarWrapper.setSizeFull();
        calendarWrapper.addComponent(calendar);

        Panel calendarPanel = new Panel();
        ((Layout) calendarPanel.getContent()).setMargin(false);
        calendarPanel.setStyleName("calendar-panel");
        calendarPanel.setSizeFull();
        calendarPanel.addComponent(toolbar);
        calendarPanel.addComponent(calendarWrapper);
        addComponent(calendarPanel);

        detailsPanel = new EventDetailsPanel();
        addComponent(detailsPanel);
        detailsPanel.setVisible(false); // hide at first

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

            detailsPanel.setEvent(devoxxCalEvent);
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == calendarMode) {
            if (calendarMode.booleanValue()) {
                calendar.addStyleName("my-schedule");
            } else {
                calendar.removeStyleName("my-schedule");
            }
        }
    }

    public void valueChange(ValueChangeEvent event) {
        calendar.setDate((Date) daySelector.getValue());
    }
}

package org.vaadin.devoxx2k10.ui.view;

import java.util.Calendar;
import java.util.Date;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.ui.FullScreenButton;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendar;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxEventProvider;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;

/**
 * The main view of the application displaying navigation, calendar and details.
 */
public class MainView extends HorizontalLayout
        implements EventClickHandler, ValueChangeListener, FragmentChangedListener {

    private static final long serialVersionUID = 7622207451668068454L;

    private DevoxxCalendar calendar;
    private DevoxxCalendarEvent selectedEvent;
    private EventDetailsPanel detailsPanel;
    private HorizontalLayout toolbar;
    private DaySelectorField daySelector;
    private FullScreenButton fullScreenButton;
    private Label dayLabel;
    private UriFragmentUtility uriFragment;

    public MainView() {
        initUi();
    }

    private void initUi() {
        setWidth("100%");
        setHeight("100%");

        dayLabel = new Label();
        dayLabel.setStyleName("selected-day");

        calendar = new DevoxxCalendar();
        calendar.setHandler(this);
        calendar.setDate(DevoxxCalendar.getDefaultDate());

        uriFragment = new UriFragmentUtility();
        uriFragment.addListener(this);
        addComponent(uriFragment);

        daySelector = new DaySelectorField(DevoxxCalendar.DEVOXX_FIRST_DAY,DevoxxCalendar.DEVOXX_LAST_DAY, uriFragment);
        daySelector.addListener(this);
        daySelector.setValue(DevoxxCalendar.getDefaultDate());

        toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setHeight("33px");
        toolbar.setStyleName("v-toolbar");
        toolbar.addComponent(new UserLayout(calendar));
        toolbar.addComponent(daySelector);
        Label placeHolder = new Label("");
        placeHolder.setWidth("250px");
        toolbar.addComponent(placeHolder);
        toolbar.setComponentAlignment(daySelector, Alignment.TOP_CENTER);
        toolbar.setExpandRatio(daySelector, 1.0f);

        fullScreenButton = new FullScreenButton(false);
        fullScreenButton.setVisible(!iOSUserAgent());
        final CssLayout calendarWrapper = new CssLayout();
        calendarWrapper.setMargin(true);
        calendarWrapper.setSizeFull();
        calendarWrapper.addComponent(fullScreenButton);
        calendarWrapper.addComponent(dayLabel);
        calendarWrapper.addComponent(calendar);

        final Panel calendarPanel = new Panel(new VerticalLayout());
        ((Layout) calendarPanel.getContent()).setMargin(false);
        calendarPanel.setStyleName("calendar-panel");
        calendarPanel.setSizeFull();
        calendarPanel.addComponent(toolbar);
        calendarPanel.addComponent(calendarWrapper);
        final Link vaadinLink = new Link("", new ExternalResource("http://vaadin.com"));
        vaadinLink.setHeight("45px");
        vaadinLink.setStyleName("vaadin");
        calendarPanel.addComponent(vaadinLink);
        ((VerticalLayout) calendarPanel.getContent()).setComponentAlignment(vaadinLink, Alignment.BOTTOM_RIGHT);
        addComponent(calendarPanel);

        detailsPanel = new EventDetailsPanel();
        addComponent(detailsPanel);
        detailsPanel.setVisible(false); // hide at first

        // make the calendar expand to use all available space
        setExpandRatio(calendarPanel, 1f);
    }

    private boolean iOSUserAgent() {
        if (DevoxxScheduleApplication.getCurrentInstance().getContext() instanceof WebApplicationContext) {
            WebBrowser browser = ((WebApplicationContext) DevoxxScheduleApplication
                    .getCurrentInstance().getContext()).getBrowser();
            String userAgent = browser.getBrowserApplication();

            if (userAgent.contains("iPod") || userAgent.contains("iPhone") || userAgent.contains("iPad")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void eventClick(final EventClick event) {
        final CalendarEvent calEvent = event.getCalendarEvent();
        selectCalendarEvent(calEvent);
    }

    private void selectCalendarEvent(final CalendarEvent calEvent) {
        if (calEvent instanceof DevoxxCalendarEvent) {
            detailsPanel.setVisible(true);

            if (selectedEvent != null) {
                selectedEvent.removeStyleName("selected");
            }

            final DevoxxCalendarEvent devoxxCalEvent = (DevoxxCalendarEvent) calEvent;
            selectedEvent = devoxxCalEvent;
            devoxxCalEvent.addStyleName("selected");

            if (!fullScreenButton.isFullScreen()) {
                getWindow().scrollIntoView(toolbar);
            }

            // Set the URI fragment for deep linking to a single event or remove
            // the URI fragment.
            if (devoxxCalEvent.getDevoxxEvent().getId() > 0) {
                uriFragment.setFragment(Integer.toString(devoxxCalEvent.getDevoxxEvent().getId()), false);
            } else {
                uriFragment.setFragment("", false);
            }

            detailsPanel.setEvent(devoxxCalEvent);
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        calendar.setDate((Date) daySelector.getValue());
        dayLabel.setValue(getLabelForDate((Date) daySelector.getValue()));
    }

    private String getLabelForDate(final Date value) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(value);

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
            return "University day 1";
        case Calendar.TUESDAY:
            return "University day 2";
        case Calendar.WEDNESDAY:
            return "Conference day 1";
        case Calendar.THURSDAY:
            return "Conference day 2";
        case Calendar.FRIDAY:
            return "Conference day 3";
        }

        return "";
    }

    @Override
    public void fragmentChanged(final FragmentChangedEvent source) {
        final String fragment = source.getUriFragmentUtility().getFragment();
        try {
            // try to parse the fragment as id
            final int id = Integer.valueOf(fragment);
            final DevoxxCalendarEvent event =
                    (DevoxxCalendarEvent) ((DevoxxEventProvider) calendar.getEventProvider()).getEvent(id);
            if (event != null) {
                // select correct date and event
                daySelector.setValue(event.getDevoxxEvent().getFromTime());
                selectCalendarEvent(event);
            }
        } catch (NumberFormatException e) {
            // the fragment was not an integer -> simply ignore
        }
    }
}

package org.vaadin.devoxx2k10.ui.view;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.RestApiException;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Panel for displaying details of the given DevoxxPresentationImpl.
 */
public class EventDetailsPanel extends Panel implements Button.ClickListener {

    private static final long serialVersionUID = -671137262550574991L;

    private final Logger logger = Logger.getLogger(getClass());

    private DevoxxCalendarEvent event;

    private CustomLayout layout;
    private Label roomLabel;
    private Label timeLabel;
    private Label titleLabel;
    private Label abstractLabel;
    private Button addToFavouritesButton;
    private Button removeFromFavouritesButton;
    private Button hideButton;
    private Label trackLabel;
    private VerticalLayout speakers;

    public EventDetailsPanel() {
        setWidth("310px");
        setHeight("100%");
        initUi();

        setStyleName("event-details-panel");
    }

    private void initUi() {
        // create components
        roomLabel = new Label();
        timeLabel = new Label();
        titleLabel = new Label();
        abstractLabel = new Label();
        trackLabel = new Label();
        addToFavouritesButton = new Button("I'm attending", this);
        removeFromFavouritesButton = new Button("I'm not attending", this);
        hideButton = new Button("Hide Event Details", this);
        hideButton.setStyleName(BaseTheme.BUTTON_LINK);
        speakers = new VerticalLayout();
        speakers.setMargin(true);
        speakers.setSpacing(true);
        speakers.setStyleName("speakers-layout");

        // add to the layout
        layout = new CustomLayout("event-details");
        setContent(layout);
        layout.addComponent(roomLabel, "room");
        layout.addComponent(timeLabel, "time");
        layout.addComponent(titleLabel, "title");
        layout.addComponent(addToFavouritesButton, "attending-button");
        layout.addComponent(abstractLabel, "abstract");
        layout.addComponent(hideButton, "hide-button");
        layout.addComponent(trackLabel, "track");
        layout.addComponent(speakers, "speakers");

        if (event != null) {
            updateEventDetails();
        }
    }

    private static String getEventFromTo(DevoxxPresentation event) {
        SimpleDateFormat dateFormatFrom = new SimpleDateFormat("EEEEE, HH:mm");
        SimpleDateFormat dateFormatTo = new SimpleDateFormat("HH:mm");

        String duration;
        long durationInMinutes = (event.getToTime().getTime() - event
                .getFromTime().getTime()) / 1000 / 60;
        if (durationInMinutes >= 60) {
            long durationInFullHours = (durationInMinutes / 60);
            duration = durationInFullHours + "h";
            if (durationInMinutes % 60 != 0) {
                duration += " "
                        + (durationInMinutes - 60 * durationInFullHours)
                        + "min";
            }
        } else {
            duration = durationInMinutes + "min";
        }

        return dateFormatFrom.format(event.getFromTime()) + " - "
                + dateFormatTo.format(event.getToTime()) + " (" + duration
                + ")";
    }

    public void setEvent(DevoxxCalendarEvent devoxxEvent) {
        event = devoxxEvent;
        updateEventDetails();
    }

    private void updateEventDetails() {
        DevoxxPresentation presentation = event.getDevoxxEvent();

        roomLabel.setValue(presentation.getRoom());
        timeLabel.setValue(getEventFromTo(presentation));
        titleLabel.setValue(presentation.getTitle());
        abstractLabel.setValue(presentation.getSummary());

        if (presentation.getTrack() != null
                && presentation.getExperience() != null) {
            trackLabel.setValue(presentation.getTrack() + " ("
                    + presentation.getExperience() + ")");
            trackLabel.setVisible(true);
        } else {
            trackLabel.setVisible(false);
        }

        speakers.removeAllComponents();
        for (DevoxxSpeaker speaker : presentation.getSpeakers()) {
            speakers.addComponent(new SpeakerDetails(speaker));
        }
        speakers.setVisible(!presentation.getSpeakers().isEmpty());

        addToFavouritesButton.setData(event);
        removeFromFavouritesButton.setData(event);
        addToFavouritesButton.setVisible(event.getDevoxxEvent().getId() > 0);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == hideButton) {
            setVisible(false);
        } else if (event.getButton() == addToFavouritesButton
                || event.getButton() == removeFromFavouritesButton) {

            DevoxxCalendarEvent calEvent = (DevoxxCalendarEvent) event
                    .getButton().getData();

            MyScheduleUser user = (MyScheduleUser) DevoxxScheduleApplication
                    .getCurrentInstance().getUser();
            if (user != null && user.getActivationCode() != null) {
                if (event.getButton() == addToFavouritesButton) {
                    addToMySchedule(user, calEvent);
                } else {
                    removeFromMySchedule(user, calEvent);
                }
            } else {
                // display login window
                getWindow().addWindow(new LoginWindow());
            }
        }
    }

    private void removeFromMySchedule(MyScheduleUser user,
            DevoxxCalendarEvent event) {
        try {
            user.getFavourites()
                    .remove((Object) event.getDevoxxEvent().getId());

            RestApiFacade facade = DevoxxScheduleApplication
                    .getCurrentInstance().getBackendFacade();
            facade.saveMySchedule(user);
            event.removeStyleName("attending");
            layout.replaceComponent(removeFromFavouritesButton,
                    addToFavouritesButton);
        } catch (RestApiException e) {
            getWindow().showNotification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void addToMySchedule(MyScheduleUser user, DevoxxCalendarEvent event) {
        try {
            user.getFavourites().add(event.getDevoxxEvent().getId());

            RestApiFacade facade = DevoxxScheduleApplication
                    .getCurrentInstance().getBackendFacade();
            facade.saveMySchedule(user);
            event.addStyleName("attending");
            layout.replaceComponent(addToFavouritesButton,
                    removeFromFavouritesButton);
        } catch (RestApiException e) {
            getWindow().showNotification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        }
    }
}

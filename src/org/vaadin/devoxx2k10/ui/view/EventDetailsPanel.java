package org.vaadin.devoxx2k10.ui.view;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
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
    private Button attendingButton;
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
        attendingButton = new Button("I'm attending", this);
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
        layout.addComponent(attendingButton, "attending-button");
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
        attendingButton.setData(event);
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
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == hideButton) {
            setVisible(false);
        } else if (event.getButton() == attendingButton) {
            DevoxxCalendarEvent calEvent = (DevoxxCalendarEvent) event
                    .getButton().getData();
            calEvent.addStyleName("attending");

            // TODO: implement the backend for the MySchedule
            logger.error("MySchedule not yet implemented!");
        }
    }
}

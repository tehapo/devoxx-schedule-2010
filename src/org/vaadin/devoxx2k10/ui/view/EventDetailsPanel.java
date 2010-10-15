package org.vaadin.devoxx2k10.ui.view;

import java.text.SimpleDateFormat;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;

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

    private DevoxxPresentation event;

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
        attendingButton = new Button("I'm attending");
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
        // layout.addComponent(attendingButton, "attending-button");
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

    public void setEvent(DevoxxPresentation devoxxEvent) {
        event = devoxxEvent;
        updateEventDetails();
    }

    private void updateEventDetails() {
        roomLabel.setValue(event.getRoom());
        timeLabel.setValue(getEventFromTo(event));
        titleLabel.setValue(event.getTitle());
        attendingButton.setData(event);
        abstractLabel.setValue(event.getSummary());

        if (event.getTrack() != null && event.getExperience() != null) {
            trackLabel.setValue(event.getTrack() + " (" + event.getExperience()
                    + ")");
            trackLabel.setVisible(true);
        } else {
            trackLabel.setVisible(false);
        }

        speakers.removeAllComponents();
        for (DevoxxSpeaker speaker : event.getSpeakers()) {
            speakers.addComponent(new SpeakerDetails(speaker));
        }
        speakers.setVisible(!event.getSpeakers().isEmpty());
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == hideButton) {
            setVisible(false);
        }
    }
}

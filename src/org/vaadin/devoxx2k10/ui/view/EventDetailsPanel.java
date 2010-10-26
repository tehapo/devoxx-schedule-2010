package org.vaadin.devoxx2k10.ui.view;

import java.text.SimpleDateFormat;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.RestApiException;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;
import org.vaadin.devoxx2k10.util.StringUtil;

import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Panel for displaying details of a given {@link DevoxxPresentation} containing
 * also functionality for adding and removing the given
 * {@link DevoxxPresentation} as a MySchedule favourite.
 */
public class EventDetailsPanel extends Panel implements Button.ClickListener,
        UserChangeListener {

    private static final long serialVersionUID = -671137262550574991L;

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
        DevoxxScheduleApplication.getCurrentInstance().addListener(this);
    }

    private void initUi() {
        // create components
        roomLabel = new Label();
        timeLabel = new Label();
        titleLabel = new Label();
        abstractLabel = new Label();
        trackLabel = new Label();
        addToFavouritesButton = new Button("I'm attending", this);
        removeFromFavouritesButton = new Button("I'm attending", this);
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
        layout.addComponent(removeFromFavouritesButton, "not-attending-button");
        layout.addComponent(abstractLabel, "abstract");
        layout.addComponent(hideButton, "hide-button");
        layout.addComponent(trackLabel, "track");
        layout.addComponent(speakers, "speakers");

        if (event != null) {
            updateEventDetails();
        }
    }

    /**
     * Set the currently selected {@link DevoxxCalendarEvent}.
     * 
     * @param devoxxEvent
     */
    public void setEvent(DevoxxCalendarEvent devoxxEvent) {
        event = devoxxEvent;
        updateEventDetails();
    }

    /**
     * Update UI components to display the details of the currently selected
     * event and currently active user.
     * 
     * @see #updateFavouriteButtons()
     */
    private void updateEventDetails() {
        DevoxxPresentation presentation = event.getDevoxxEvent();

        roomLabel.setValue(presentation.getRoom());
        timeLabel.setValue(getEventTimeLabel(presentation));
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

        updateFavouriteButtons();
    }

    /**
     * Returns the given {@link DevoxxPresentation} start time, end time and
     * duration in a pretty formatted String.
     * 
     * @param presentation
     * @return
     */
    private String getEventTimeLabel(DevoxxPresentation presentation) {
        SimpleDateFormat dateFormatFrom = new SimpleDateFormat("EEEEE, HH:mm");
        SimpleDateFormat dateFormatTo = new SimpleDateFormat("HH:mm");

        return dateFormatFrom.format(presentation.getFromTime()) + " - "
                + dateFormatTo.format(presentation.getToTime()) + " ("
                + StringUtil.getEventDuration(presentation) + ")";
    }

    /**
     * Update only the add and remove to favourites buttons visibility and data
     * content to reflect the current user and currently selected event. This
     * method is also called by the {@link #updateEventDetails()} method.
     * 
     * @see #updateEventDetails()
     */
    private void updateFavouriteButtons() {
        DevoxxPresentation presentation = event.getDevoxxEvent();

        addToFavouritesButton.setData(event);
        addToFavouritesButton.setVisible(true);
        removeFromFavouritesButton.setData(event);
        removeFromFavouritesButton.setVisible(false);

        MyScheduleUser user = (MyScheduleUser) getApplication().getUser();
        if (event.getDevoxxEvent().getId() > 0) {
            if (user != null && user.hasFavourited(presentation)) {
                // Show the remove button instead of add button.
                addToFavouritesButton.setVisible(false);
                removeFromFavouritesButton.setVisible(true);
            }
        } else {
            // This presentation is not applicable for adding to MySchedule (no
            // id defined).
            addToFavouritesButton.setVisible(false);
        }
    }

    public void buttonClick(final ClickEvent event) {
        if (event.getButton() == hideButton) {
            // hide this panel
            setVisible(false);
        } else if (event.getButton() == addToFavouritesButton
                || event.getButton() == removeFromFavouritesButton) {

            // we're adding or removing a favourite
            final DevoxxCalendarEvent calEvent = (DevoxxCalendarEvent) event
                    .getButton().getData();

            if (getLoggedInUser() != null) {
                handleAddOrRemoveClick(event.getButton(), calEvent);
            } else {
                // display login window
                Window loginWindow = new LoginWindow();
                getWindow().addWindow(loginWindow);

                loginWindow.addListener(new CloseListener() {
                    private static final long serialVersionUID = -513395997260118984L;

                    public void windowClose(CloseEvent e) {
                        // try again after the window is closed
                        handleAddOrRemoveClick(event.getButton(), calEvent);
                    }
                });
            }
        }
    }

    private MyScheduleUser getLoggedInUser() {
        MyScheduleUser user = (MyScheduleUser) DevoxxScheduleApplication
                .getCurrentInstance().getUser();
        if (user != null && user.getActivationCode() != null) {
            return user;
        }
        return null;
    }

    private void handleAddOrRemoveClick(Button clickedButton,
            DevoxxCalendarEvent calEvent) {
        MyScheduleUser user = getLoggedInUser();
        if (user != null) {
            boolean addToFavourites = true;
            if (clickedButton == removeFromFavouritesButton) {
                addToFavourites = false;
            }
            try {
                if (addToFavourites) {
                    user.addFavourite(event.getDevoxxEvent());
                } else {
                    user.removeFavourite(event.getDevoxxEvent());
                }

                RestApiFacade facade = DevoxxScheduleApplication
                        .getCurrentInstance().getBackendFacade();
                facade.saveMySchedule(user);

                if (addToFavourites) {
                    event.addStyleName("attending");
                } else {
                    event.removeStyleName("attending");
                }
                updateFavouriteButtons();
            } catch (RestApiException e) {
                getWindow().showNotification(e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }

    public void applicationUserChanged(UserChangeEvent event) {
        if (this.event != null) {
            updateFavouriteButtons();
        }
    }

}

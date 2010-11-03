package org.vaadin.devoxx2k10.ui.view;

import java.text.SimpleDateFormat;

import org.vaadin.addthis.AddThis;
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
import com.vaadin.ui.CssLayout;
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
public class EventDetailsPanel extends Panel implements Button.ClickListener, UserChangeListener {

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
    private AddThis addThis;
    private CssLayout tags;
    private RelatedTalksLayout relatedTalks;

    public EventDetailsPanel(final MainView mainView) {
        setWidth("310px");
        setHeight("100%");
        initUi(mainView);

        setStyleName("event-details-panel");
        DevoxxScheduleApplication.getCurrentInstance().addListener(this);
    }

    private void initUi(final MainView mainView) {
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
        addThis = new AddThis();
        addThis.addButton("twitter");
        addThis.addButton("facebook");
        addThis.addButton("google");
        addThis.addButton("mailto");
        tags = new CssLayout();
        relatedTalks = new RelatedTalksLayout(mainView);

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
        layout.addComponent(addThis, "add-this");
        layout.addComponent(tags, "tags");

        if (event != null) {
            updateEventDetails();
        }
    }

    /**
     * Set the currently selected {@link DevoxxCalendarEvent}.
     * 
     * @param devoxxEvent
     */
    public void setEvent(final DevoxxCalendarEvent devoxxEvent) {
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
        final DevoxxPresentation presentation = event.getDevoxxEvent();

        roomLabel.setValue(presentation.getRoom() + " " + presentation.getRoomExtraInfo());
        timeLabel.setValue(getEventTimeLabel(presentation));
        titleLabel.setValue(presentation.getTitle());
        abstractLabel.setValue(presentation.getSummary());

        if (presentation.getTrack() != null && presentation.getExperience() != null) {
            trackLabel.setValue(presentation.getTrack() + " (" + presentation.getExperience() + ")");
            trackLabel.setVisible(true);
        } else {
            trackLabel.setVisible(false);
        }

        speakers.removeAllComponents();
        for (final DevoxxSpeaker speaker : presentation.getSpeakers()) {
            speakers.addComponent(new SpeakerDetails(speaker));
        }
        speakers.setVisible(!presentation.getSpeakers().isEmpty());

        if (presentation.getId() > 0) {
            addThis.setVisible(true);
            addThis.setUrl(getApplication().getURL() + "presentation/" + presentation.getId());
            addThis.setTitle(presentation.getTitle());
        } else {
            addThis.setVisible(false);
        }

        tags.removeAllComponents();
        if (presentation.getTags() != null && !presentation.getTags().isEmpty()) {
            updateTags(presentation);
        }

        updateFavouriteButtons();
    }

    private void updateTags(final DevoxxPresentation presentation) {
        final Label keywordsLabel = new Label("Keywords: ");
        keywordsLabel.setSizeUndefined();
        tags.addComponent(keywordsLabel);
        boolean first = true;
        for (final String tag : presentation.getTags()) {
            if (!first) {
                final Label comma = new Label(", ");
                comma.setSizeUndefined();
                tags.addComponent(comma);
            }
            first = false;
            final Button tagButton = new Button(tag, this);
            tagButton.setData(tag);
            tagButton.setStyleName(BaseTheme.BUTTON_LINK);
            tags.addComponent(tagButton);
        }
        relatedTalks.setVisible(false);
        tags.addComponent(relatedTalks);
    }

    /**
     * Returns the given {@link DevoxxPresentation} start time, end time and
     * duration in a pretty formatted String.
     * 
     * @param presentation
     * @return
     */
    private String getEventTimeLabel(final DevoxxPresentation presentation) {
        final SimpleDateFormat dateFormatFrom = new SimpleDateFormat("EEEEE, HH:mm");
        final SimpleDateFormat dateFormatTo = new SimpleDateFormat("HH:mm");

        return dateFormatFrom.format(presentation.getFromTime()) + " - " + dateFormatTo.format(presentation.getToTime())
                + " (" + StringUtil.getEventDuration(presentation) + ")";
    }

    /**
     * Update only the add and remove to favourites buttons visibility and data
     * content to reflect the current user and currently selected event. This
     * method is also called by the {@link #updateEventDetails()} method.
     * 
     * @see #updateEventDetails()
     */
    private void updateFavouriteButtons() {
        final DevoxxPresentation presentation = event.getDevoxxEvent();

        addToFavouritesButton.setData(event);
        addToFavouritesButton.setVisible(true);
        removeFromFavouritesButton.setData(event);
        removeFromFavouritesButton.setVisible(false);

        final MyScheduleUser user = (MyScheduleUser) getApplication().getUser();
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
        } else if (event.getButton() == addToFavouritesButton || event.getButton() == removeFromFavouritesButton) {

            // we're adding or removing a favourite
            final DevoxxCalendarEvent calEvent = (DevoxxCalendarEvent) event.getButton().getData();

            if (getLoggedInUser() != null) {
                handleAddOrRemoveClick(event.getButton(), calEvent);
            } else {
                // display login window
                final Window loginWindow = new LoginWindow();
                getWindow().addWindow(loginWindow);

                loginWindow.addListener(new CloseListener() {
                    private static final long serialVersionUID = -513395997260118984L;

                    public void windowClose(final CloseEvent e) {
                        // try again after the window is closed
                        handleAddOrRemoveClick(event.getButton(), calEvent);
                    }
                });
            }
        } else if (event.getButton().getData() instanceof String) {
            // assume a tag button
            final String tag = (String) event.getButton().getData();
            relatedTalks.setTag(tag, this.event.getDevoxxEvent());
            relatedTalks.setVisible(true);
        }
    }

    private MyScheduleUser getLoggedInUser() {
        final MyScheduleUser user = (MyScheduleUser) DevoxxScheduleApplication.getCurrentInstance().getUser();
        if (user != null && user.getActivationCode() != null) {
            return user;
        }
        return null;
    }

    private void handleAddOrRemoveClick(final Button clickedButton, final DevoxxCalendarEvent calEvent) {
        final MyScheduleUser user = getLoggedInUser();
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

                final RestApiFacade facade = DevoxxScheduleApplication.getCurrentInstance().getBackendFacade();
                facade.saveMySchedule(user);

                if (addToFavourites) {
                    event.addStyleName("attending");
                } else {
                    event.removeStyleName("attending");
                }
                updateFavouriteButtons();
            } catch (final RestApiException e) {
                getWindow().showNotification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }

    public void applicationUserChanged(final UserChangeEvent event) {
        if (this.event != null) {
            updateFavouriteButtons();
        }
    }

}

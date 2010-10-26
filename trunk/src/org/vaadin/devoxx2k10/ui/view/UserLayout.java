package org.vaadin.devoxx2k10.ui.view;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser.UserFavouritesChangedListener;

import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

public class UserLayout extends CssLayout implements ClickListener,
        UserChangeListener, UserFavouritesChangedListener {

    private static final long serialVersionUID = 7016536111338741020L;

    private Button signInButton;
    private Button signOutButton;
    private Label currentUserLabel;
    private Button calendarMode;
    private final Calendar calendar;

    public UserLayout(Calendar calendar) {
        setStyleName("user-layout");
        setWidth("250px");

        this.calendar = calendar;
        currentUserLabel = new Label("");
        currentUserLabel.setWidth("");
        currentUserLabel.setStyleName("current-user");
        signInButton = new Button("Sign Into MySchedule", this);
        signInButton.setStyleName("sign-in-button");
        signOutButton = new Button("Sign Out", this);
        signOutButton.setStyleName(BaseTheme.BUTTON_LINK);
        signOutButton.addStyleName("sign-out-button");
        calendarMode = new Button("", this);
        calendarMode.setStyleName("calendar-mode");
        calendarMode.setData(Boolean.FALSE);
        calendarMode.setDescription("Highlight events I'm attending");
        calendarMode.setImmediate(true);

        addComponent(calendarMode);
        addComponent(currentUserLabel);
        addComponent(signInButton);
        addComponent(signOutButton);

        DevoxxScheduleApplication.getCurrentInstance().addListener(this);
        updateComponentVisibility();
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == signInButton) {
            getWindow().addWindow(new LoginWindow());
        } else if (event.getButton() == signOutButton) {
            getApplication().setUser(null);
        } else if (event.getButton() == calendarMode) {
            boolean currentMode = (Boolean) calendarMode.getData();
            setMyScheduleMode(!currentMode);
        }
    }

    private void setMyScheduleMode(boolean active) {
        if (active) {
            calendar.addStyleName("my-schedule");
            calendarMode.addStyleName("my-schedule");
            calendarMode.setData(Boolean.TRUE);
        } else {
            calendar.removeStyleName("my-schedule");
            calendarMode.removeStyleName("my-schedule");
            calendarMode.setData(Boolean.FALSE);
        }
    }

    private void updateFavouriteCount() {
        Object user = DevoxxScheduleApplication.getCurrentInstance().getUser();
        int favouriteCount = 0;
        if (user != null && user instanceof MyScheduleUser) {
            favouriteCount = ((MyScheduleUser) user).getFavourites().size();
        }

        calendarMode.setCaption(Integer.toString(favouriteCount));
    }

    private void updateComponentVisibility() {
        boolean userLoggedIn = DevoxxScheduleApplication.getCurrentInstance()
                .getUser() != null;
        calendarMode.setVisible(userLoggedIn);
        currentUserLabel.setVisible(userLoggedIn);
        signInButton.setVisible(!userLoggedIn);
        signOutButton.setVisible(userLoggedIn);
    }

    public void applicationUserChanged(UserChangeEvent event) {
        if (getApplication().getUser() != null
                && getApplication().getUser() instanceof MyScheduleUser) {
            MyScheduleUser newUser = (MyScheduleUser) getApplication()
                    .getUser();
            String label = newUser.getEmail();
            if (label.length() > 20) {
                label = label.substring(0, 17) + "..";
            }
            currentUserLabel.setValue(label);
            newUser.addListener(this);
        }
        setMyScheduleMode(false);
        updateComponentVisibility();
        updateFavouriteCount();
    }

    public void favouritesChanged(MyScheduleUser user) {
        updateFavouriteCount();
    }
}

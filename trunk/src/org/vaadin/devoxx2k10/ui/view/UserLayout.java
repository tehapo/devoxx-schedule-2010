package org.vaadin.devoxx2k10.ui.view;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class UserLayout extends CssLayout implements ClickListener,
        UserChangeListener {

    private static final long serialVersionUID = 7016536111338741020L;

    private Button signInButton;
    private Button signOutButton;
    private Label currentUserLabel;

    private static final String NOT_SIGNED_IN_XHTML = "<i>Not signed in</i>";

    public UserLayout() {
        setStyleName("user-layout");

        currentUserLabel = new Label(NOT_SIGNED_IN_XHTML, Label.CONTENT_XHTML);
        signInButton = new Button("Sign In", this);
        signOutButton = new Button("Sign Out", this);
        signOutButton.setVisible(false);

        addComponent(currentUserLabel);
        addComponent(signInButton);
        addComponent(signOutButton);

        DevoxxScheduleApplication.getCurrentInstance().addListener(this);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == signInButton) {
            getWindow().addWindow(new LoginWindow());
        } else if (event.getButton() == signOutButton) {
            getApplication().setUser(null);
        }
    }

    public void applicationUserChanged(UserChangeEvent event) {
        if (getApplication().getUser() != null
                && getApplication().getUser() instanceof MyScheduleUser) {
            MyScheduleUser newUser = (MyScheduleUser) getApplication()
                    .getUser();
            currentUserLabel.setValue(newUser.getEmail());
        } else {
            currentUserLabel.setValue(NOT_SIGNED_IN_XHTML);
        }

        signInButton.setVisible(getApplication().getUser() == null);
        signOutButton.setVisible(getApplication().getUser() != null);
    }
}

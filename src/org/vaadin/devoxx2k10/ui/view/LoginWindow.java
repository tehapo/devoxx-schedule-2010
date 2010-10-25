package org.vaadin.devoxx2k10.ui.view;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.RestApiException;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class LoginWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 5020323273015528048L;

    private Logger logger = Logger.getLogger(getClass());

    private TextField activateFirstName;
    private TextField activateLastName;
    private TextField activateEmail;
    private Button activateButton;
    private Label activateDoneLabel;
    private Label activateInstructionsLabel;
    private Button showActivateButton;

    private TextField signInEmail;
    private TextField signInActivationCode;
    private Button signInButton;
    private Label signInInstructionsLabel;

    private VerticalLayout activateLayout;
    private VerticalLayout signInLayout;

    public LoginWindow() {
        super("Sign in to MySchedule");
        setStyleName("login-window");
        setModal(true);
        setWidth("235px");
        setResizable(false);
        setContent(createLayout());
    }

    private Layout createLayout() {

        // create all required fields
        activateInstructionsLabel = new Label(
                "Activate MySchedule by providing your name and e-mail address. After activation you'll receive an activation code to the given e-mail address.");
        activateFirstName = createTextField("First Name");
        activateLastName = createTextField("Last Name");
        activateEmail = createTextField("E-mail");
        activateEmail.addValidator(new EmailValidator(
                "Not a valid e-mail address"));
        activateButton = new Button("Activate", this);
        activateDoneLabel = new Label(
                "<strong>Activation code sent!</strong> Check your inbox and copy the code to the field below and you're ready to go.",
                Label.CONTENT_XHTML);
        activateDoneLabel.setVisible(false);

        signInEmail = createTextField("E-mail");
        signInEmail.addValidator(new EmailValidator(
                "Not a valid e-mail address"));
        signInActivationCode = createTextField("Activation Code");
        signInButton = new Button("Sign In", this);
        signInButton.setClickShortcut(KeyCode.ENTER);
        signInInstructionsLabel = new Label(
                "Sign in by providing your e-mail address and activation code.");
        showActivateButton = new Button("Don't yet have an activation code?",
                this);
        showActivateButton.setStyleName(BaseTheme.BUTTON_LINK);

        // add the created fields to a Layout
        Layout layout = new VerticalLayout();

        signInLayout = new VerticalLayout();
        signInLayout.setSpacing(true);
        signInLayout.setSizeFull();
        signInLayout.setStyleName("sign-in");
        signInLayout.addComponent(activateDoneLabel);
        signInLayout.addComponent(signInInstructionsLabel);
        signInLayout.addComponent(signInEmail);
        signInLayout.addComponent(signInActivationCode);
        signInLayout.addComponent(signInButton);
        signInLayout.addComponent(showActivateButton);
        layout.addComponent(signInLayout);

        activateLayout = new VerticalLayout();
        activateLayout.setSpacing(true);
        activateLayout.setSizeFull();
        activateLayout.setStyleName("activate");
        activateLayout.setVisible(false);
        activateLayout.addComponent(activateInstructionsLabel);
        activateLayout.addComponent(activateFirstName);
        activateLayout.addComponent(activateLastName);
        activateLayout.addComponent(activateEmail);
        activateLayout.addComponent(activateButton);
        layout.addComponent(activateLayout);

        return layout;
    }

    private TextField createTextField(String caption) {
        TextField field = new TextField(caption);
        field.setValidationVisible(false);
        field.setRequired(true);
        field.setRequiredError("Required field");
        field.setWidth("100%");
        return field;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == signInButton) {
            doSignIn();
        } else if (event.getButton() == activateButton) {
            doActivate();
        } else if (event.getButton() == showActivateButton) {
            showActivateLayout();
        }
    }

    private void showActivateLayout() {
        activateLayout.setVisible(true);
        activateButton.setClickShortcut(KeyCode.ENTER);
        signInLayout.setVisible(false);
        signInButton.removeClickShortcut();
    }

    private void hideActivateLayout() {
        activateLayout.setVisible(false);
        activateButton.removeClickShortcut();
        signInLayout.setVisible(true);
        signInButton.setClickShortcut(KeyCode.ENTER);
    }

    private void doActivate() {
        activateEmail.setValidationVisible(true);
        activateFirstName.setValidationVisible(true);
        activateLastName.setValidationVisible(true);
        if (!activateEmail.isValid() || !activateFirstName.isValid()
                || !activateLastName.isValid()) {
            return;
        }

        DevoxxScheduleApplication app = DevoxxScheduleApplication
                .getCurrentInstance();
        try {
            // tell the backend to do the activation
            app.getBackendFacade().activateMySchedule(
                    (String) activateFirstName.getValue(),
                    (String) activateLastName.getValue(),
                    (String) activateEmail.getValue());

            // hide the activation fields and show sign in fields again
            hideActivateLayout();

            // show further instructions and copy the e-mail to sign in field
            activateDoneLabel.setVisible(true);
            signInEmail.setValue(activateEmail.getValue());
        } catch (RestApiException e) {
            logger.error(e.getMessage(), e);
            getWindow().showNotification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void doSignIn() {
        signInEmail.setValidationVisible(true);
        signInActivationCode.setValidationVisible(true);
        if (!signInEmail.isValid() || !signInActivationCode.isValid()) {
            return;
        }

        MyScheduleUser newUser = new MyScheduleUser(
                (String) signInEmail.getValue(),
                (String) signInActivationCode.getValue());

        // load the favourites for this user from the backend
        DevoxxScheduleApplication app = DevoxxScheduleApplication
                .getCurrentInstance();
        app.getBackendFacade().getScheduleForUser(newUser);

        // set the new user instance as the logged in user
        getApplication().setUser(newUser);

        // close this modal window
        close();
    }

}

package org.vaadin.devoxx2k10;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.log4j.Logger;
import org.vaadin.browsercookies.BrowserCookies;
import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.RestApiException;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.ui.view.MainView;
import org.vaadin.devoxx2k10.ui.view.ScheduleGATracker;
import org.vaadin.devoxx2k10.ui.view.UnsupportedBrowserWindow;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Window;

/**
 * Vaadin Application for displaying Devoxx 2010 schedules using the Vaadin
 * Calendar add-on for the main display and the Devoxx REST interface as the
 * data source for the schedule data.
 * 
 * @link http://www.devoxx.com
 * @link http://www.vaadin.com
 * @link http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface
 * @link http://vaadin.com/addon/vaadin-calendar
 */
public class DevoxxScheduleApplication extends Application implements TransactionListener, BrowserCookies.UpdateListener {

    private static final String COOKIE_FIELD_SEPARATOR = ",";
    private static final long COOKIE_EXPIRATION_IN_MILLIS = 365 * 24 * 60 * 60 * 1000L;

    private static final Logger logger = Logger.getLogger(DevoxxScheduleApplication.class);
    public static final String MY_SCHEDULE_USER_COOKIE = "MyScheduleUser";

    private static final long serialVersionUID = 1167695727109405960L;

    private static final CustomizedSystemMessages systemMessages;

    // Use the ThreadLocal pattern, for more details see:
    // http://vaadin.com/wiki/-/wiki/Main/ThreadLocal%20Pattern
    private static final ThreadLocal<DevoxxScheduleApplication> currentApplication = new ThreadLocal<DevoxxScheduleApplication>();

    private transient RestApiFacade backendFacade;

    private BrowserCookies cookies;
    private GoogleAnalyticsTracker tracker;

    static {
        systemMessages = new CustomizedSystemMessages();

        // Disable session expired notification -> just restart the application
        // if the session expires.
        systemMessages.setSessionExpiredNotificationEnabled(false);
    }

    public static SystemMessages getSystemMessages() {
        return systemMessages;
    }

    @Override
    public String getVersion() {
        return VersionInformation.getVersion();
    }

    @Override
    public void init() {
        currentApplication.set(this);
        getContext().addTransactionListener(this);

        backendFacade = new CachingRestApiFacade();

        setMainWindow(createMainWindow());
        setTheme("devoxx2k10");
    }

    private Window createMainWindow() {
        final Window mainWindow = new Window("Devoxx 2010 Schedule");

        // init Google Analytics tracker
        tracker = new ScheduleGATracker();

        final MainView mainView = new MainView();
        mainWindow.setContent(mainView);
        mainWindow.addURIHandler(mainView);

        checkBrowserSupport(mainWindow);

        // init cookie handling
        cookies = new BrowserCookies(true);
        cookies.addListener(this);
        mainWindow.addComponent(cookies);
        mainWindow.addComponent(tracker);

        return mainWindow;
    }

    /**
     * Track a page view with Google Analytics.
     * 
     * @param action
     *            name of the action to be tracked.
     * @param target
     *            target DevoxxPresentation for the action (null allowed).
     */
    public static void trackPageview(final String action, final DevoxxPresentation target) {
        String path = "/" + action;
        if (target != null) {
            try {
                path += "/" + target.getId() + "/" + URLEncoder.encode(target.getTitle(), "utf-8");
            } catch (final UnsupportedEncodingException ignored) {
                // should never happen
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Tracking page view: " + path);
        }

        getCurrentInstance().tracker.trackPageview(path);
    }

    public void storeUserCookie() {
        if (getUser() != null && getUser() instanceof MyScheduleUser) {
            final MyScheduleUser user = (MyScheduleUser) getUser();
            final String cookieData = user.getEmail() + COOKIE_FIELD_SEPARATOR + user.getActivationCode();
            final Date cookieExpiration = new Date(System.currentTimeMillis() + COOKIE_EXPIRATION_IN_MILLIS);

            cookies.setCookie(MY_SCHEDULE_USER_COOKIE, cookieData, cookieExpiration);
        }
    }

    public void doSignOut() {
        setUser(null);
        cookies.setCookie(MY_SCHEDULE_USER_COOKIE, "-", new Date(1));
    }

    @Override
    public void cookiesUpdated(final BrowserCookies browserCookies) {
        final String myScheduleUser = browserCookies.getCookie(MY_SCHEDULE_USER_COOKIE);
        if (myScheduleUser != null && DevoxxScheduleApplication.getCurrentInstance().getUser() == null) {
            final String[] userData = myScheduleUser.split(COOKIE_FIELD_SEPARATOR);
            if (userData.length == 2) {
                try {
                    doSignIn(userData[0], userData[1]);
                } catch (final RestApiException e) {
                    logger.error("Sign in from cookie data failed: " + e.getMessage(), e);
                }
            }
        }
    }

    private void checkBrowserSupport(final Window mainWindow) {
        if (getContext() instanceof WebApplicationContext) {
            final WebBrowser browser = ((WebApplicationContext) getContext()).getBrowser();

            if (!isSupportedBrowser(browser)) {
                mainWindow.addWindow(new UnsupportedBrowserWindow("http://devoxx.com"));
            }
        }
    }

    private boolean isSupportedBrowser(final WebBrowser browser) {
        return !(browser.isIE() && browser.getBrowserMajorVersion() <= 6);
    }

    public boolean doSignIn(final String email, final String activationCode) throws RestApiException {
        if (email == null || activationCode == null || !Configuration.getBooleanProperty("myschedule.enabled")) {
            return false;
        }

        final MyScheduleUser newUser = new MyScheduleUser(email, activationCode);

        if (getBackendFacade().isValidUser(newUser)) {
            // valid user -> load the favourites for this user from the backend
            getBackendFacade().getScheduleForUser(newUser);

            // set the new user instance as the logged in user
            setUser(newUser);
            return true;
        }

        return false;
    }

    /**
     * Returns the facade for calling the backend methods for retrieving or
     * storing data.
     * 
     * @return the facade for calling the backend.
     */
    public RestApiFacade getBackendFacade() {
        return backendFacade;
    }

    /**
     * Returns the instance of this Application for the currently running Thread
     * for easy access to this instance.
     * 
     * @return instance of this Application for the currently running Thread.
     */
    public static DevoxxScheduleApplication getCurrentInstance() {
        return currentApplication.get();
    }

    @Override
    public Window getWindow(final String name) {
        Window window = super.getWindow(name);
        if (window == null) {
            // support for multiple browser windows or tabs
            if (logger.isDebugEnabled()) {
                logger.debug("Creating new Window for name: " + name);
            }
            window = createMainWindow();
            window.setName(name);
            addWindow(window);
        }
        return window;
    }

    public void transactionStart(final Application application, final Object transactionData) {
        if (application == this) {
            // set the ThreadLocal value
            currentApplication.set(this);
        }
    }

    public void transactionEnd(final Application application, final Object transactionData) {
        if (application == this) {
            // remove the ThreadLocal value
            currentApplication.remove();
        }
    }

}

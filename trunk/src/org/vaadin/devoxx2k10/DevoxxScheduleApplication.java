package org.vaadin.devoxx2k10;

import org.vaadin.devoxx2k10.data.CachingRestApiFacade;
import org.vaadin.devoxx2k10.data.RestApiFacade;
import org.vaadin.devoxx2k10.ui.view.MainView;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.Window;

/**
 * Vaadin Application for displaying Devoxx 2010 schedules using the Vaadin
 * Calendar add-on for the main display and the Devoxx REST interface as the
 * data source for the schedule data.
 * 
 * @see http://www.devoxx.com
 * @see http://www.vaadin.com
 * @see http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface
 * @see http://vaadin.com/addon/vaadin-calendar
 */
public class DevoxxScheduleApplication extends Application implements
        TransactionListener {

    private static final long serialVersionUID = 1167695727109405960L;

    private static final CustomizedSystemMessages systemMessages;

    // Use the ThreadLocal pattern, for more details see:
    // http://vaadin.com/wiki/-/wiki/Main/ThreadLocal%20Pattern
    private static final ThreadLocal<DevoxxScheduleApplication> currentApplication = new ThreadLocal<DevoxxScheduleApplication>();

    private transient RestApiFacade backendFacade;

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
    public void init() {
        currentApplication.set(this);
        getContext().addTransactionListener(this);

        backendFacade = new CachingRestApiFacade();

        Window mainWindow = new Window("Devoxx 2010 Schedule");
        setMainWindow(mainWindow);
        setTheme("devoxx2k10");

        MainView mainView = new MainView();
        mainWindow.setContent(mainView);
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

    public void transactionStart(Application application, Object transactionData) {
        if (application == this) {
            // set the ThreadLocal value
            currentApplication.set(this);
        }
    }

    public void transactionEnd(Application application, Object transactionData) {
        if (application == this) {
            // remove the ThreadLocal value
            currentApplication.remove();
        }
    }

}

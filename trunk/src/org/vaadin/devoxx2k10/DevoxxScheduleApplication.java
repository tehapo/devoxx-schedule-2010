package org.vaadin.devoxx2k10;

import org.vaadin.devoxx2k10.ui.view.MainView;

import com.vaadin.Application;
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
public class DevoxxScheduleApplication extends Application {

    private static final long serialVersionUID = 1167695727109405960L;

    private static final CustomizedSystemMessages systemMessages;

    static {
        systemMessages = new CustomizedSystemMessages();

        // Disable session expired notification -> just restart the application
        // if the session expires.
        systemMessages.setSessionExpiredNotificationEnabled(false);
    }

    @Override
    public void init() {
        Window mainWindow = new Window("Devoxx 2010 Schedule");
        setMainWindow(mainWindow);
        setTheme("devoxx2k10");

        MainView mainView = new MainView();
        mainWindow.setContent(mainView);
    }

    public static SystemMessages getSystemMessages() {
        return systemMessages;
    }

}

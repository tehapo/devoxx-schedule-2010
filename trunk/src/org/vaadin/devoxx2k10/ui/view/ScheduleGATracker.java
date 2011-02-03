package org.vaadin.devoxx2k10.ui.view;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.Configuration;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

public class ScheduleGATracker extends GoogleAnalyticsTracker {

    private static final long serialVersionUID = 7361545770417805481L;

    private static final String TRACKER_ID;
    private static final String DOMAIN = "none";

    static {
        TRACKER_ID = Configuration.getProperty("google.analytics.tracker.id");
        if (TRACKER_ID == null) {
            Logger.getLogger(ScheduleGATracker.class).warn(
                    "Google Analytics tracking disabled (missing configuration param: google.analytics.tracker.id)");
        }
    }

    public ScheduleGATracker() {
        super(TRACKER_ID, DOMAIN);
    }

}

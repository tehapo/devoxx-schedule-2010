package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser.UserFavouritesChangedListener;

import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.addon.calendar.ui.Calendar;

/**
 * DevoxxCalendar is a Calendar UI component for displaying the Devoxx
 * conference schedule.
 */
public class DevoxxCalendar extends Calendar implements UserChangeListener, UserFavouritesChangedListener {

    private static final long serialVersionUID = -3068684747425348483L;

    private final Logger logger = Logger.getLogger(getClass());

    /** First day of Devoxx 2010 */
    public static final Date DEVOXX_FIRST_DAY;

    /** Last day of Devoxx 2010 */
    public static final Date DEVOXX_LAST_DAY;

    static {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2010, 10, 15, 0, 0);
        DEVOXX_FIRST_DAY = cal.getTime();
        cal.set(2010, 10, 19, 23, 59);
        DEVOXX_LAST_DAY = cal.getTime();
    }

    public DevoxxCalendar() {
        super();

        // set up the appearance
        setTimeFormat(TimeFormat.Format24H);
        setVisibleHoursOfDay(8, 21);
        setWidth("100%");
        setHeight("1700px");
        setReadOnly(true);

        // set up the event provider
        setEventProvider(new DevoxxEventProvider());

        // Attach this Calendar as a UserChangeListener and
        // UserFavouritesChangedListener if there already is a signed in user.
        final DevoxxScheduleApplication app = DevoxxScheduleApplication.getCurrentInstance();
        app.addListener(this);
        if (app.getUser() instanceof MyScheduleUser) {
            ((MyScheduleUser) app.getUser()).addListener(this);
        }
    }

    /**
     * Sets the displayed date to the given date if it's during Devoxx,
     * otherwise does nothing.
     * 
     * @param date
     */
    public void setDate(final Date date) {
        if (isDuringDevoxx(date)) {
            setStartDate(date);
            setEndDate(date);
        }
    }

    /**
     * Returns the default date, which is the first day of Devoxx or the current
     * date if it is during Devoxx.
     * 
     * @return the first day of Devoxx or the current date.
     */
    public static Date getDefaultDate() {
        Date defaultDate = new Date();
        if (!isDuringDevoxx(defaultDate)) {
            defaultDate = DEVOXX_FIRST_DAY;
        }
        return defaultDate;
    }

    private static boolean isDuringDevoxx(final Date date) {
        return date != null && date.compareTo(DEVOXX_FIRST_DAY) >= 0 && date.compareTo(DEVOXX_LAST_DAY) <= 0;
    }

    private void refreshCalendarStyles() {
        if (getEventProvider() instanceof DevoxxEventProvider) {
            ((DevoxxEventProvider) getEventProvider()).refreshAttendingStyles();
        }
    }

    @Override
    public void applicationUserChanged(final UserChangeEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("User has changed, requesting refresh of the Calendar styles.");
        }

        refreshCalendarStyles();
        if (event.getNewUser() instanceof MyScheduleUser) {
            ((MyScheduleUser) event.getNewUser()).addListener(this);
        }
    }

    @Override
    public void favouritesChanged(final MyScheduleUser user) {
        refreshCalendarStyles();
    }
}

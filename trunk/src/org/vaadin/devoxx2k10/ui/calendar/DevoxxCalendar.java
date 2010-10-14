package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;

import com.vaadin.addon.calendar.ui.Calendar;

/**
 * DevoxxCalendar is a Calendar UI component for displaying the Devoxx
 * conference schedule.
 */
public class DevoxxCalendar extends Calendar {

    private static final long serialVersionUID = -3068684747425348483L;

    /** First day of Devoxx 2010 */
    public static final Date DEVOXX_FIRST_DAY;

    /** Last day of Devoxx 2010 */
    public static final Date DEVOXX_LAST_DAY;

    static {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2010, 10, 15, 00, 00);
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
    }

    /**
     * Sets the displayed date to the given date if it's during Devoxx,
     * otherwise does nothing.
     * 
     * @param date
     */
    public void setDate(Date date) {
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

    private static boolean isDuringDevoxx(Date date) {
        if (date != null) {
            return date.compareTo(DEVOXX_FIRST_DAY) >= 0
                    && date.compareTo(DEVOXX_LAST_DAY) <= 0;
        }
        return false;
    }

}

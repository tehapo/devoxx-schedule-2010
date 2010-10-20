package org.vaadin.devoxx2k10.util;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;

public class StringUtil {

    /**
     * Returns the duration of given {@link DevoxxPresentation} as hours and
     * minutes (for example "1h 20min") or just in minutes if the duration is
     * under one hour (for example "35min").
     * 
     * @param event
     * @return
     */
    public static String getEventDuration(DevoxxPresentation event) {
        String duration;
        long durationInMinutes = (event.getToTime().getTime() - event
                .getFromTime().getTime()) / 1000 / 60;
        if (durationInMinutes >= 60) {
            long durationInFullHours = (durationInMinutes / 60);
            duration = durationInFullHours + "h";
            if (durationInMinutes % 60 != 0) {
                duration += " "
                        + (durationInMinutes - 60 * durationInFullHours)
                        + "min";
            }
        } else {
            duration = durationInMinutes + "min";
        }

        return duration;
    }

}

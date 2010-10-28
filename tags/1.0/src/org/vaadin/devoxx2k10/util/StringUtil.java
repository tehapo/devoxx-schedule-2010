package org.vaadin.devoxx2k10.util;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;

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

    /**
     * Returns the speakers of the event as a String in parenthesis separated
     * with a comma or an empty String if there are no speakers for the event.
     * 
     * @param event
     * @return the speakers of the event as a String in parenthesis separated
     *         with a comma or an empty String.
     */
    public static String getSpeakersString(DevoxxPresentation event) {
        StringBuilder speakers = new StringBuilder();
        if (!event.getSpeakers().isEmpty()) {
            speakers.append('(');
            for (DevoxxSpeaker speaker : event.getSpeakers()) {
                if (speakers.length() > 1) {
                    speakers.append(", ");
                }
                speakers.append(speaker.getName());
            }
            speakers.append(')');
        }
        return speakers.toString();
    }

}

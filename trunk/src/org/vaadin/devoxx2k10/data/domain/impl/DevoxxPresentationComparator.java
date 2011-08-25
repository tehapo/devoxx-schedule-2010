package org.vaadin.devoxx2k10.data.domain.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.Configuration;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;

/**
 * Compares the DevoxxPresentations so that they are in chronological order
 * according to the start time and ordered by the room size (from smallest to
 * the largest) when having the same starting time.
 */
public class DevoxxPresentationComparator implements Comparator<DevoxxPresentation> {

    /** Predefined room order of Devoxx */
    private static final List<String> conferenceRoomOrder;

    static {
        conferenceRoomOrder = Arrays.asList(Configuration.getArrayProperty("conference.room.ordering"));
        if (!conferenceRoomOrder.isEmpty()) {
            Logger.getLogger(DevoxxPresentationComparator.class).info("Using room ordering: " + conferenceRoomOrder);
        }
    }

    public int compare(final DevoxxPresentation o1, final DevoxxPresentation o2) {
        final int dateCompare = o1.getFromTime().compareTo(o2.getFromTime());
        if (dateCompare == 0) {
            // compare against the predefined room ordering
            return conferenceRoomOrder.indexOf(o2.getRoom()) - conferenceRoomOrder.indexOf(o1.getRoom());
        }
        return dateCompare;
    }
}

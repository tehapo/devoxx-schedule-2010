package org.vaadin.devoxx2k10.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;

/**
 * Compares the DevoxxPresentations so that they are in chronological order
 * according to the start time and ordered by the room size (from smallest to
 * the largest) when having the same starting time.
 */
public class DevoxxPresentationComparator implements
        Comparator<DevoxxPresentation> {

    /** Predefined room order of Devoxx */
    private static final List<String> devoxxRoomOrder;

    static {
        devoxxRoomOrder = new ArrayList<String>();
        devoxxRoomOrder.add("Room 8");
        devoxxRoomOrder.add("Room 5");
        devoxxRoomOrder.add("Room 4");
        devoxxRoomOrder.add("Room 9");
        devoxxRoomOrder.add("Room 6");
        devoxxRoomOrder.add("Room 7");
        devoxxRoomOrder.add("BOF 1");
        devoxxRoomOrder.add("BOF 2");
    }

    public int compare(DevoxxPresentation o1, DevoxxPresentation o2) {
        int dateCompare = o1.getFromTime().compareTo(o2.getFromTime());
        if (dateCompare == 0) {
            // compare against the predefined room ordering
            return devoxxRoomOrder.indexOf(o2.getRoom())
                    - devoxxRoomOrder.indexOf(o1.getRoom());
        }
        return dateCompare;
    }

}

package org.vaadin.devoxx2k10.tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.devoxx2k10.data.DevoxxPresentationComparator;
import org.vaadin.devoxx2k10.data.DevoxxPresentationImpl;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentationKind;

public class TestDevoxxPresentationComparator {

    private static final DevoxxPresentation year2009room8;
    private static final DevoxxPresentation year2010room8;
    private static final DevoxxPresentation year2010room7;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2009);
        year2009room8 = new DevoxxPresentationImpl(1, cal.getTime(),
                cal.getTime(), "", "", DevoxxPresentationKind.KEYNOTE, null,
                null, "Room 8", false, null);

        cal.set(Calendar.YEAR, 2010);
        year2010room8 = new DevoxxPresentationImpl(1, cal.getTime(),
                cal.getTime(), "", "", DevoxxPresentationKind.KEYNOTE, null,
                null, "Room 8", false, null);

        year2010room7 = new DevoxxPresentationImpl(1, cal.getTime(),
                cal.getTime(), "", "", DevoxxPresentationKind.KEYNOTE, null,
                null, "Room 7", false, null);
    }

    @Test
    public void testComparatorDate() {
        List<DevoxxPresentation> list = new ArrayList<DevoxxPresentation>(2);
        list.add(year2010room8);
        list.add(year2009room8);

        Assert.assertEquals(0, list.indexOf(year2010room8));
        Assert.assertEquals(1, list.indexOf(year2009room8));

        Collections.sort(list, new DevoxxPresentationComparator());

        Assert.assertEquals(0, list.indexOf(year2009room8));
        Assert.assertEquals(1, list.indexOf(year2010room8));
    }

    @Test
    public void testComparatorRoom() {
        List<DevoxxPresentation> list = new ArrayList<DevoxxPresentation>(2);
        list.add(year2010room8);
        list.add(year2010room7);

        Assert.assertEquals(0, list.indexOf(year2010room8));
        Assert.assertEquals(1, list.indexOf(year2010room7));

        Collections.sort(list, new DevoxxPresentationComparator());

        Assert.assertEquals(0, list.indexOf(year2010room7));
        Assert.assertEquals(1, list.indexOf(year2010room8));
    }
}

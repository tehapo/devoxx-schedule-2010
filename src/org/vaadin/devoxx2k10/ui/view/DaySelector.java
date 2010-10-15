package org.vaadin.devoxx2k10.ui.view;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;

/**
 * CustomField for selecting days from a certain date interval given in the
 * constructor.
 */
public class DaySelector extends CustomField implements Button.ClickListener,
        Property.ValueChangeListener {

    private static final long serialVersionUID = 2754744076391844451L;

    private ComponentContainer layout;

    public DaySelector(Date firstDay, Date lastDay) {
        layout = new VerticalLayout();
        setCompositionRoot(layout);
        addListener(this);

        Calendar javaCalendar = Calendar.getInstance();
        javaCalendar.setTime(firstDay);

        while (javaCalendar.getTime().compareTo(lastDay) <= 0) {
            String dayName = javaCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG, Locale.US);

            Button dayButton = new Button(dayName);
            dayButton.setDescription(dayName);
            dayButton.setStyleName(dayName.toLowerCase());
            dayButton.setData(javaCalendar.getTime());
            dayButton.addListener((ClickListener) this);
            layout.addComponent(dayButton);

            // next day
            javaCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public void buttonClick(ClickEvent event) {
        Button clicked = event.getButton();
        setValue((Date) clicked.getData());
    }

    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        // set the style name
        for (Iterator<Component> it = layout.getComponentIterator(); it
                .hasNext();) {
            Button b = (Button) it.next();

            if (dayEquals((Date) b.getData(), (Date) getValue())) {
                b.addStyleName("selected");
            } else {
                b.removeStyleName("selected");
            }
        }
    }

    /**
     * Compares the two given dates ignoring the time of day.
     * 
     * @param first
     * @param second
     * @return true if the dates represent the same day.
     */
    private boolean dayEquals(Date first, Date second) {
        Calendar firstCal = Calendar.getInstance();
        firstCal.setTime(first);
        Calendar secondCal = Calendar.getInstance();
        secondCal.setTime(second);

        return firstCal.get(Calendar.DAY_OF_YEAR) == secondCal
                .get(Calendar.DAY_OF_YEAR)
                && firstCal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR);

    }

    @Override
    public Class<?> getType() {
        return Date.class;
    }

}

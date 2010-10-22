package org.vaadin.devoxx2k10.ui.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;

/**
 * CustomField for selecting days from a certain date interval given in the
 * constructor.
 */
public class DaySelectorField extends CustomField implements
        Button.ClickListener, Property.ValueChangeListener {

    private static final long serialVersionUID = -4777985742354898074L;

    private ComponentContainer layout;
    private List<Button> dayButtons = new ArrayList<Button>();

    public DaySelectorField(Date firstDay, Date lastDay) {
        layout = new CssLayout();
        setCompositionRoot(layout);
        setStyleName("day-selector-field");
        addListener(this);

        Calendar javaCalendar = Calendar.getInstance();
        javaCalendar.setTime(firstDay);

        while (javaCalendar.getTime().compareTo(lastDay) <= 0) {
            String dayName = javaCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.SHORT, Locale.US);

            Button dayButton = new Button(dayName, (Button.ClickListener) this);
            dayButton.setStyleName("day-button");
            dayButton.setData(javaCalendar.getTime());
            layout.addComponent(dayButton);
            dayButtons.add(dayButton);

            // next day
            javaCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (dayButtons.size() > 0) {
            dayButtons.get(0).addStyleName("first");
            dayButtons.get(dayButtons.size() - 1).addStyleName("last");
        }
    }

    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        // set the style name for selected
        for (Button b : dayButtons) {
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

    public void buttonClick(ClickEvent event) {
        Date clickedValue = (Date) event.getButton().getData();
        setValue(clickedValue);
    }

}

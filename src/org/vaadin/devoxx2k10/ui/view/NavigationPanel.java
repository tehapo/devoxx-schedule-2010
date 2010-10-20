package org.vaadin.devoxx2k10.ui.view;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendar;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class NavigationPanel extends CssLayout implements ValueChangeListener {

    private static final long serialVersionUID = -4150447116715964273L;

    private DaySelector daySelector;
    private List<DateChangeListener> listeners = new LinkedList<DateChangeListener>();

    public NavigationPanel(Date initialDateValue) {
        setStyleName("navigation");
        setHeight("100%");

        addComponent(new UserLayout());

        addComponent(new Label("Schedule"));

        daySelector = new DaySelector(DevoxxCalendar.DEVOXX_FIRST_DAY,
                DevoxxCalendar.DEVOXX_LAST_DAY);
        daySelector.addListener((ValueChangeListener) this);
        daySelector.setValue(initialDateValue);
        addComponent(daySelector);
    }

    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty() == daySelector) {
            notifyListeners((Date) daySelector.getValue());
        }
    }

    public void addListener(DateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DateChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Date newDate) {
        for (DateChangeListener listener : listeners) {
            listener.dateChanged(newDate);
        }
    }

    /**
     * Interface for listening changes in the selected date.
     */
    public static interface DateChangeListener {

        public void dateChanged(Date newDate);

    }

}

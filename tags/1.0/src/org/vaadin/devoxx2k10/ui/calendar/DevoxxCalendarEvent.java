package org.vaadin.devoxx2k10.ui.calendar;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.util.StringUtil;

import com.vaadin.addon.calendar.event.BasicEvent;

public class DevoxxCalendarEvent extends BasicEvent {

    private static final long serialVersionUID = -5372113758406385246L;

    private DevoxxPresentation devoxxEvent;
    private Set<String> additionalStyles = new HashSet<String>();
    private String speakers;

    @Override
    public Date getStart() {
        return devoxxEvent.getFromTime();
    }

    @Override
    public Date getEnd() {
        return devoxxEvent.getToTime();
    }

    @Override
    public String getCaption() {
        if (speakers == null) {
            speakers = StringUtil.getSpeakersString(devoxxEvent);
        }
        return devoxxEvent.getTitle() + " " + speakers;
    }

    public DevoxxPresentation getDevoxxEvent() {
        return devoxxEvent;
    }

    public void setDevoxxEvent(DevoxxPresentation devoxxEvent) {
        this.devoxxEvent = devoxxEvent;
    }

    /**
     * Overridden to allow multiple CSS class names.
     */
    @Override
    public String getStyleName() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getStyleName());
        for (String styleName : additionalStyles) {
            sb.append(" ");
            sb.append(styleName);
        }
        return sb.toString();
    }

    public void addStyleName(String styleName) {
        boolean added = additionalStyles.add(styleName);
        if (added) {
            fireEventChange();
        }
    }

    public void removeStyleName(String styleName) {
        boolean removed = additionalStyles.remove(styleName);
        if (removed) {
            fireEventChange();
        }
    }
}

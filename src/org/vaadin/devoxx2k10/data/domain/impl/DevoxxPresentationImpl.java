package org.vaadin.devoxx2k10.data.domain.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.vaadin.devoxx2k10.data.LazyLoad;
import org.vaadin.devoxx2k10.data.LazyLoadable;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentationKind;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;

public class DevoxxPresentationImpl implements DevoxxPresentation, LazyLoadable {

    private final int id;
    private final Date fromTime;
    private final Date toTime;
    private final String code;
    private final String type;
    private final DevoxxPresentationKind kind;
    private final String title;
    private final List<DevoxxSpeaker> speakers;
    private final String room;
    private final boolean partnerSlot;
    private final String presentationUri;

    private volatile String summary;
    private volatile String track;
    private volatile String experience;
    private volatile Set<String> tags;

    public DevoxxPresentationImpl(final int id, final Date fromTime, final Date toTime, final String code,
            final String type, final DevoxxPresentationKind kind, final String title, final List<DevoxxSpeaker> speakers,
            final String room, final boolean partnerSlot, final String presentationUri) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.code = code;
        this.type = type;
        this.kind = kind;
        this.title = title;
        this.speakers = speakers;
        this.room = room;
        this.partnerSlot = partnerSlot;
        this.presentationUri = presentationUri;
    }

    public String getType() {
        return type;
    }

    public DevoxxPresentationKind getKind() {
        return kind;
    }

    public boolean isPartnerSlot() {
        return partnerSlot;
    }

    public int getId() {
        return id;
    }

    public Date getToTime() {
        return toTime;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public String getCode() {
        return code;
    }

    public String getRoom() {
        return room;
    }

    public String getRoomExtraInfo() {
        if (kind == DevoxxPresentationKind.KEYNOTE) {
            return "(Overflow in rooms 5 and 4)";
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public List<DevoxxSpeaker> getSpeakers() {
        return speakers;
    }

    public String getLazyLoadingUri() {
        return presentationUri;
    }

    @LazyLoad("summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    @LazyLoad("track")
    public String getTrack() {
        return track;
    }

    public void setTrack(final String track) {
        this.track = track;
    }

    @LazyLoad("experience")
    public String getExperience() {
        return experience;
    }

    public void setExperience(final String experience) {
        this.experience = experience;
    }

    @LazyLoad("tags/name")
    public Set<String> getTags() {
        return tags;
    }

    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DevoxxPresentation)) {
            return false;
        }
        final DevoxxPresentation other = (DevoxxPresentation) obj;
        return id == other.getId();
    }
}

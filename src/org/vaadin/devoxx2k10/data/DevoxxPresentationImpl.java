package org.vaadin.devoxx2k10.data;

import java.util.Date;
import java.util.List;

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

    public DevoxxPresentationImpl(int id, Date fromTime, Date toTime,
            String code, String type, DevoxxPresentationKind kind,
            String title, List<DevoxxSpeaker> speakers, String room,
            boolean partnerSlot, String presentationUri) {
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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @LazyLoad("track")
    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    @LazyLoad("experience")
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

}

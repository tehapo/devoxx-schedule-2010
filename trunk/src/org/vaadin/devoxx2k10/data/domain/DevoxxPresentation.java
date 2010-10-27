package org.vaadin.devoxx2k10.data.domain;

import java.util.Date;
import java.util.List;

public interface DevoxxPresentation {

    public String getType();

    public DevoxxPresentationKind getKind();

    public boolean isPartnerSlot();

    public int getId();

    public Date getFromTime();

    public Date getToTime();

    public String getCode();

    public String getRoom();

    public String getRoomExtraInfo();

    public String getTitle();

    public List<DevoxxSpeaker> getSpeakers();

    public String getSummary();

    public String getTrack();

    public String getExperience();

}

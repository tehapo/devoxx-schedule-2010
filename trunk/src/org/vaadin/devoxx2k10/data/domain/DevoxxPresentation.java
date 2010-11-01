package org.vaadin.devoxx2k10.data.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface DevoxxPresentation {

    String getType();

    DevoxxPresentationKind getKind();

    boolean isPartnerSlot();

    int getId();

    Date getFromTime();

    Date getToTime();

    String getCode();

    String getRoom();

    String getRoomExtraInfo();

    String getTitle();

    List<DevoxxSpeaker> getSpeakers();

    String getSummary();

    String getTrack();

    String getExperience();

    Set<String> getTags();
}

package org.vaadin.devoxx2k10.data.domain;

public enum DevoxxPresentationKind {

    REGISTRATION, TALK, LUNCH, BREAK, BREAKFAST, COFFEE_BREAK, KEYNOTE;

    public boolean isSpeak() {
        return this == KEYNOTE || this == TALK;
    }

}

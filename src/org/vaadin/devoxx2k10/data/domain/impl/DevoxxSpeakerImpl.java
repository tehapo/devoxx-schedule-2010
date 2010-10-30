package org.vaadin.devoxx2k10.data.domain.impl;

import org.vaadin.devoxx2k10.data.LazyLoad;
import org.vaadin.devoxx2k10.data.LazyLoadable;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;

/**
 * DevoxxSpeaker implementation that uses lazy loading for certain details.
 * 
 * @see org.vaadin.devoxx2k10.data.LazyLoadable
 * @see org.vaadin.devoxx2k10.data.LazyLoad
 */
public final class DevoxxSpeakerImpl implements DevoxxSpeaker, LazyLoadable {

    private final String name;
    private final String speakerUri;

    private volatile int id;
    private volatile String imageUri;
    private volatile String bio;

    public DevoxxSpeakerImpl(final String name, final String speakerUri) {
        this.name = name;
        this.speakerUri = speakerUri;
    }

    @LazyLoad("id")
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @LazyLoad("imageURI")
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(final String imageUri) {
        this.imageUri = imageUri;
    }

    @LazyLoad("bio")
    public String getBio() {
        return bio;
    }

    public void setBio(final String bio) {
        this.bio = bio;
    }

    public String getLazyLoadingUri() {
        return speakerUri;
    }

}

package org.vaadin.devoxx2k10.data.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Details of a user of the MySchedule feature.
 */
public class MyScheduleUser {

    private String email;
    private String activationCode;
    private List<Integer> favourites;

    public MyScheduleUser(String email, String activationCode) {
        this.email = email;
        this.activationCode = activationCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public List<Integer> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<Integer> favourites) {
        this.favourites = favourites;
    }

    public void addFavourite(DevoxxPresentation presentation) {
        if (favourites == null) {
            favourites = new ArrayList<Integer>();
        }
        favourites.add(presentation.getId());
    }

    public void removeFavourite(DevoxxPresentation presentation) {
        if (favourites != null) {
            // Must cast to Integer to avoid calling remove by index.
            favourites.remove((Integer) presentation.getId());
        }
    }

    public boolean hasFavourited(DevoxxPresentation presentation) {
        if (favourites != null) {
            return favourites.contains(presentation.getId());
        }
        return false;
    }
}

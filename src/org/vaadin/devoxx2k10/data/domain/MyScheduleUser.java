package org.vaadin.devoxx2k10.data.domain;

import java.util.List;

/**
 * Details of a user of the MySchedule feature.
 */
public class MyScheduleUser {

    private String email;
    private String activationCode;
    private List<Integer> favourites;

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
}

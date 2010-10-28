package org.vaadin.devoxx2k10.data;

import java.util.List;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

/**
 * Facade interface for the Devoxx REST API. All calls from the UI to the REST
 * API should be made through this interface.
 * 
 * @see http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface
 */
public interface RestApiFacade {

    public void activateMySchedule(String firstName, String lastName,
            String email) throws RestApiException;

    public void saveMySchedule(MyScheduleUser user) throws RestApiException;

    /**
     * Returns true if the given user's e-mail and activation code are valid for
     * a user of MySchedule feature.
     * 
     * @param email
     * @param activationCode
     * @return
     * @throws RestApiException
     */
    public boolean isValidUser(MyScheduleUser user) throws RestApiException;

    public void getScheduleForUser(MyScheduleUser user);

    public List<DevoxxPresentation> getFullSchedule();

}

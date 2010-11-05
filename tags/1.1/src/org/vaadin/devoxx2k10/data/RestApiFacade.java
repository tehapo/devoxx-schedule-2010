package org.vaadin.devoxx2k10.data;

import java.util.List;

import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

/**
 * Facade interface for the Devoxx REST API. All calls from the UI to the REST
 * API should be made through this interface.
 * 
 * @link http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface
 */
public interface RestApiFacade {

    /**
     * 
     * @param firstName
     * @param lastName
     * @param email
     * @throws RestApiException
     */
    void activateMySchedule(String firstName, String lastName, String email) throws RestApiException;

    /**
     * 
     * @param user
     * @throws RestApiException
     */
    void saveMySchedule(MyScheduleUser user) throws RestApiException;

    /**
     * Returns true if the given user's e-mail and activation code are valid for
     * a user of MySchedule feature.
     * 
     * @param user
     * @return
     * @throws RestApiException
     */
    boolean isValidUser(MyScheduleUser user) throws RestApiException;

    /**
     * 
     * @param user
     */
    void getScheduleForUser(MyScheduleUser user);

    /**
     * 
     * @return
     */
    List<DevoxxPresentation> getFullSchedule();

    /**
     * Search for {@link DevoxxPresentation}s containing the given tag.
     * 
     * @return List of DevoxxPresentations containing the given tag.
     */
    List<DevoxxPresentation> search(String tag);
}

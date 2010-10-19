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

    public void getScheduleForUser(MyScheduleUser user);

    public List<DevoxxPresentation> getFullSchedule();

    /**
     * Uses reflection to fill all fields of given LazyLoadable that are
     * decorated with the LazyLoad annotation.
     * 
     * @param lazy
     */
    public void lazyLoadFields(LazyLoadable lazyLoadable);
}

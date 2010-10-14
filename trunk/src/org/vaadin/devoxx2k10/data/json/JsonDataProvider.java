package org.vaadin.devoxx2k10.data.json;

public interface JsonDataProvider {

    /**
     * Returns the JSON describing the full schedule.
     * 
     * @return
     */
    public String getScheduleJson();

    /**
     * Returns the JSON with given URI.
     * 
     * @param presentationId
     * @return
     */
    public String getJson(String jsonUri);

}

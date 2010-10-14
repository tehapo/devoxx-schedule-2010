package org.vaadin.devoxx2k10.data.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * JsonDataProvider implementation that uses the Devoxx REST API.
 * 
 * @see http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface
 */
public class HttpJsonDataProvider implements JsonDataProvider {

    private static final int DEVOXX_EVENT_ID = 1;

    private static final String REST_API_BASE_URL = "http://cfp.devoxx.com/rest/v1";

    private static final String SCHEDULE_URL = REST_API_BASE_URL + "/events/"
            + DEVOXX_EVENT_ID + "/schedule";

    private final Logger logger = Logger.getLogger(getClass());

    public String getScheduleJson() {
        try {
            return get(SCHEDULE_URL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSpeakerJson(String speakerUri) {
        try {
            return get(speakerUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJson(String jsonUri) {
        try {
            return get(jsonUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Does a read from the given URL and returns the result as a String.
     * 
     * @param urlString
     * @return
     * @throws IOException
     */
    private String get(String urlString) throws IOException {
        logger.debug("Loading: " + urlString);
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                urlConnection.getInputStream()));
        try {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                line = in.readLine();
            }
            return result.toString();
        } finally {
            in.close();
        }
    }

}

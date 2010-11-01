package org.vaadin.devoxx2k10.data.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.log4j.Logger;

/**
 * An offline HttpClient implementation for unit tests and offline development.
 */
public class OfflineHttpClientMock implements HttpClient {

    private final Logger logger = Logger.getLogger(getClass());

    private static final String TEST_JSON_SPEAKER = "{\"id\":56,\"lastName\":\"Dashorst\",\"bio\":\"Martijn Dashorst is a senior software engineer for Topicus B.V.. He is a committer on the Wicket project and works on many Wicket related projects. When he doesn't work with or on Wicket, he likes to spend time with his girlfriend and two cats.\",\"company\":\"Topicus Onderwijs\",\"talks\":[{\"title\":\"Introducing Wicket\",\"event\":\"University (3h)\",\"presentationUri\":\"http://cfp.devoxx.com/rest/v1/events/presentations/291\"}],\"imageURI\":\"http://cfp.devoxx.com/static/images/56/thumbnail.gif\",\"firstName\":\"Martijn\"}";
    private static final String TEST_JSON_PRESENTATION = "{\"tags\":[{\"name\":\"javase\"},{\"name\":\"modules\"},{\"name\":\"closures\"},{\"name\":\"Java7\"},{\"name\":\"Java8\"}],\"summary\":\"During this keynote you'll get an overview of the state of Java SE 7 and beyond.\",\"id\":333,\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/279\",\"title\":\"Java SE: The Road Ahead\",\"speaker\":\"Mark Reinhold\",\"track\":\"Java Core (SE/EE)\",\"experience\":\"SENIOR\",\"speakers\":[{\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/279\",\"speaker\":\"Mark Reinhold\"}],\"type\":\"Conference (1h)\"}";
    private static final String TEST_JSON_USER_FAVOURITES = "[{id:115},{id:278},{id:284}]";

    private String getScheduleJson() {
        final InputStream is = getClass().getClassLoader().getResourceAsStream("offline-schedule.json");
        if (is != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                final StringBuilder jsonData = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                return jsonData.toString();
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public HttpResponse get(final String urlString) throws IOException {
        logger.debug("GET [offline]: " + urlString);
        if (urlString.startsWith("http://cfp.devoxx.com/rest/v1/events/speakers")) {
            return new HttpResponse(HttpURLConnection.HTTP_OK, TEST_JSON_SPEAKER);
        } else if (urlString.startsWith("http://cfp.devoxx.com/rest/v1/events/presentations")) {
            return new HttpResponse(HttpURLConnection.HTTP_OK, TEST_JSON_PRESENTATION);
        } else if (urlString.equals("http://cfp.devoxx.com/rest/v1/events/1/schedule")) {
            return new HttpResponse(HttpURLConnection.HTTP_OK, getScheduleJson());
        } else if (urlString.startsWith("http://cfp.devoxx.com/rest/v1/events/1/schedule/")) {
            return new HttpResponse(HttpURLConnection.HTTP_OK, TEST_JSON_USER_FAVOURITES);
        }
        throw new RuntimeException("Unknown URI " + urlString);
    }

    public int post(final String urlString, final String postData) throws IOException {
        logger.debug("POST [offline]: " + urlString + ", " + postData);
        if (urlString.startsWith("http://cfp.devoxx.com/rest/v1/events/users/validate")) {
            return HttpURLConnection.HTTP_OK;
        }
        return HttpURLConnection.HTTP_CREATED;
    }
}

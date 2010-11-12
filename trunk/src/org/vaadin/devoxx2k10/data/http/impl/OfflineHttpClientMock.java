package org.vaadin.devoxx2k10.data.http.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.data.RestApiFacadeImpl;
import org.vaadin.devoxx2k10.data.http.HttpClient;
import org.vaadin.devoxx2k10.data.http.HttpResponse;

/**
 * An offline HttpClient implementation for unit tests and offline development.
 */
public class OfflineHttpClientMock implements HttpClient {

    private final Logger logger = Logger.getLogger(getClass());

    public static final String OFFLINE_DATA_BASEDIR = "offline-json-snapshots";
    private final String prefix;

    public OfflineHttpClientMock(final String prefix) {
        this.prefix = prefix;
    }

    private String getLoadLocalJson(final String filePath) {
        logger.debug("File path: " + filePath);

        final String fullPath = OFFLINE_DATA_BASEDIR + File.separator + prefix + filePath;
        final InputStream is = getClass().getClassLoader().getResourceAsStream(fullPath);
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

        final String filePath = urlString.replace(RestApiFacadeImpl.REST_API_BASE_URL, "").replaceAll("/", File.separator);
        return new HttpResponse(HttpURLConnection.HTTP_OK, getLoadLocalJson(filePath));
    }

    public int post(final String urlString, final String postData) throws IOException {
        logger.debug("POST [offline]: " + urlString + ", " + postData);
        if (urlString.startsWith("http://cfp.devoxx.com/rest/v1/events/users/validate")) {
            return HttpURLConnection.HTTP_OK;
        }
        return HttpURLConnection.HTTP_CREATED;
    }

}

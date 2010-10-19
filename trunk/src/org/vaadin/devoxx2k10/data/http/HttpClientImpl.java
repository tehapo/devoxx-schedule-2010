package org.vaadin.devoxx2k10.data.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * A very simple HttpClient implementation.
 */
public class HttpClientImpl implements HttpClient {

    private static final String USER_AGENT = "VaadinDevoxxScheduleApp";
    private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Does an HTTP GET from the given URL and returns the response as a String.
     * 
     * @param urlString
     * @return
     * @throws IOException
     */
    public String get(String urlString) throws IOException {
        logger.debug("HTTP GET: " + urlString);
        HttpURLConnection urlConnection = openURLConnection(urlString);

        try {
            int responseCode = urlConnection.getResponseCode();
            logger.debug("Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
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
            } else {
                throw new IOException("Response code: " + responseCode);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Does an HTTP POST to the given URL and returns the response code.
     * 
     * @param urlString
     * @param postData
     * @return response code
     * @throws IOException
     */
    public int post(String urlString, String postData) throws IOException {
        logger.debug("HTTP POST: " + urlString);

        HttpURLConnection urlConnection = openURLConnection(urlString);
        urlConnection.setRequestProperty("Content-Type", POST_CONTENT_TYPE);
        urlConnection.setDoOutput(true);

        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());
            try {
                writer.write(postData);
                writer.flush();

                int responseCode = urlConnection.getResponseCode();
                logger.debug("Response code: " + responseCode);
                return responseCode;
            } finally {
                writer.close();
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private HttpURLConnection openURLConnection(String urlString)
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url
                .openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);
        return urlConnection;
    }

}

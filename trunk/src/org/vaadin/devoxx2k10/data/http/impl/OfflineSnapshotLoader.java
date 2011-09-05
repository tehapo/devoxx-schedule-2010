package org.vaadin.devoxx2k10.data.http.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.vaadin.devoxx2k10.data.LazyLoadable;
import org.vaadin.devoxx2k10.data.RestApiFacadeImpl;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.http.HttpClient;
import org.vaadin.devoxx2k10.data.http.HttpResponse;

/**
 * Loads a snapshot of the full schedule JSON data into files for offline usage.
 */
public class OfflineSnapshotLoader {

    public static void main(final String[] args) {
        final DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        final OfflineSnapshotLoader loader = new OfflineSnapshotLoader(format.format(new Date()));
        loader.execute();
    }

    private final String prefix;
    private final HttpClient httpClient;
    private final RestApiFacadeImpl facade;
    private File rootDir;

    public OfflineSnapshotLoader(final String prefix) {
        this.prefix = prefix;
        this.httpClient = new HttpClientImpl();
        this.facade = new RestApiFacadeImpl(httpClient);
    }

    public void execute() {
        rootDir = new File("src" + File.separator + OfflineHttpClientMock.OFFLINE_DATA_BASEDIR + File.separator + prefix);
        if (rootDir.exists()) {
            System.err.println(rootDir.getAbsolutePath() + " already exists!");
        } else {
            System.out.println("Loading schedule data to " + rootDir.getAbsolutePath());
            rootDir.mkdirs();
            loadSchedule();
        }
    }

    private void loadSchedule() {
        // load the schedule JSON
        saveUrlToFile(RestApiFacadeImpl.SCHEDULE_URL);

        // iterate through the actual schedule objects to load all required data
        final List<DevoxxPresentation> schedule = facade.getFullSchedule();
        for (final DevoxxPresentation presentation : schedule) {
            if (presentation instanceof LazyLoadable) {
                saveUrlToFile(((LazyLoadable) presentation).getLazyLoadingUri());
            }

            for (final DevoxxSpeaker speaker : presentation.getSpeakers()) {
                if (speaker instanceof LazyLoadable) {
                    saveUrlToFile(((LazyLoadable) speaker).getLazyLoadingUri());
                }
            }

            // be nice to servers and take a nap
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void saveUrlToFile(final String url) {
        if (url == null || url.length() == 0) {
            return;
        }

        try {
            // load data and check the response code
            final HttpResponse response = httpClient.get(url);
            if (!(response.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                System.err.println(url + " response code " + response.getResponseCode());
                return;
            }

            // response was ok
            final String scheduleJson = response.getResponse();

            final File target = createFileForUrl(url);
            if (target != null && target.exists()) {
                System.out.println(url + " -> " + target.getAbsolutePath());
                writeToFile(scheduleJson, target);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given target file must exist already.
     * 
     * @param data
     * @param target
     * @throws IOException
     */
    private void writeToFile(final String data, final File target) throws IOException {
        final FileWriter writer = new FileWriter(target);
        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }

    private File createFileForUrl(String url) throws IOException {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("Given url must not be null or empty.");
        }

        // strip possible trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // strip the base URL
        url = url.replace(RestApiFacadeImpl.REST_API_BASE_URL, "");

        // fix separator
        url = url.replaceAll("/", File.separator);

        final String dir = url.substring(0, url.lastIndexOf("/"));
        final String fileName = url.substring(url.lastIndexOf("/") + 1);

        final File directory = new File(rootDir.getAbsolutePath() + dir);
        if (directory.exists() || directory.mkdirs()) {
            final File file = new File(directory.getAbsolutePath() + File.separator + fileName);
            if (file.createNewFile()) {
                return file;
            }
        }

        return null;
    }
}

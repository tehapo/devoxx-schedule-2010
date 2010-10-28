package org.vaadin.devoxx2k10.data;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentationKind;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;
import org.vaadin.devoxx2k10.data.http.HttpClient;
import org.vaadin.devoxx2k10.data.http.HttpClientImpl;
import org.vaadin.devoxx2k10.data.http.HttpResponse;

/**
 * Facade for the Devoxx REST API.
 * 
 * You can inject your own HttpClient implementation by using the constructor
 * taking it as a parameter or you can use the default implementation by using
 * the no-arg constructor.
 */
public class RestApiFacadeImpl implements RestApiFacade, LazyLoadProvider {

    private final Logger logger = Logger.getLogger(getClass());

    private final HttpClient httpClient;

    private static final String DEVOXX_JSON_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final int DEVOXX_EVENT_ID = 1;

    private static final String REST_API_BASE_URL = "http://cfp.devoxx.com/rest/v1";

    private static final String SCHEDULE_URL = REST_API_BASE_URL + "/events/"
            + DEVOXX_EVENT_ID + "/schedule";

    private static final String MY_SCHEDULE_ACTIVATE_URL = REST_API_BASE_URL
            + "/events/users/activate";

    private static final String MY_SCHEDULE_VALIDATION_URL = REST_API_BASE_URL
            + "/events/users/validate";

    public RestApiFacadeImpl() {
        // this(new OfflineHttpClientMock());
        this(new HttpClientImpl());
    }

    public RestApiFacadeImpl(HttpClient httpClient) {
        logger.debug("Initializing RestApiFacade with HttpClient "
                + httpClient.getClass().getName());
        this.httpClient = httpClient;
    }

    public void activateMySchedule(String firstName, String lastName,
            String email) throws RestApiException {
        try {
            StringBuilder params = new StringBuilder();
            params.append("firstName=" + URLEncoder.encode(firstName, "utf-8"));
            params.append('&');
            params.append("lastName=" + URLEncoder.encode(lastName, "utf-8"));
            params.append('&');
            params.append("email=" + URLEncoder.encode(email, "utf-8"));

            int response = httpClient.post(MY_SCHEDULE_ACTIVATE_URL,
                    params.toString());
            if (response != HttpURLConnection.HTTP_CREATED) {
                logger.error("Response code: " + response);
                throw new RestApiException(
                        "MySchedule activation failed. Please try again later.");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RestApiException(
                    "MySchedule activation failed. Please try again later.");
        }
    }

    public void saveMySchedule(MyScheduleUser user) throws RestApiException {
        if (user.getActivationCode() == null || user.getEmail() == null) {
            throw new IllegalArgumentException(
                    "Activation code and e-mail must be set for the user.");
        }
        if (user.getFavourites() == null) {
            throw new IllegalArgumentException(
                    "User must have favourites to save.");
        }

        try {
            StringBuilder params = new StringBuilder();
            params.append("code="
                    + URLEncoder.encode(user.getActivationCode(), "utf-8"));
            for (Integer favouriteId : user.getFavourites()) {
                params.append('&');
                params.append("favorites=");
                params.append(favouriteId);
            }

            int response = httpClient.post(
                    SCHEDULE_URL + "/" + user.getEmail(), params.toString());

            if (response != HttpURLConnection.HTTP_CREATED) {
                logger.error("Response code: " + response);
                if (response == HttpURLConnection.HTTP_CONFLICT) {
                    user.setActivationCode(null);
                    throw new RestApiException(
                            "Activation code rejected. Please try signing in again.");

                }
                throw new RestApiException(
                        "Adding to MySchedule failed. Please try again later.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidUser(MyScheduleUser user) throws RestApiException {
        try {
            StringBuilder params = new StringBuilder();
            params.append("email="
                    + URLEncoder.encode(user.getEmail(), "utf-8"));
            params.append('&');
            params.append("code="
                    + URLEncoder.encode(user.getActivationCode(), "utf-8"));

            int response = httpClient.post(MY_SCHEDULE_VALIDATION_URL,
                    params.toString());
            if (response == HttpURLConnection.HTTP_OK) {
                return true;
            } else if (response == HttpURLConnection.HTTP_CONFLICT) {
                return false;
            } else {
                logger.error("Response code: " + response);
                throw new RestApiException(
                        "MySchedule validation failed. Please try again later.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getScheduleForUser(MyScheduleUser user) {
        if (user != null && user.getEmail() != null) {
            try {
                HttpResponse response = httpClient.get(SCHEDULE_URL + "/"
                        + user.getEmail());

                if (response.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                    // user has no favourites yet
                    user.setFavourites(new HashSet<Integer>());
                } else if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // parse the response
                    user.setFavourites(getScheduleIds(httpClient.get(
                            SCHEDULE_URL + "/" + user.getEmail()).getResponse()));
                }

                if (user.getFavourites() != null) {
                    logger.debug("Retrieved " + user.getFavourites().size()
                            + " favourites for user " + user.getEmail());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<DevoxxPresentation> getFullSchedule() {
        try {
            return getScheduleData(httpClient.get(SCHEDULE_URL).getResponse());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Set<Integer> getScheduleIds(String scheduleJson) {
        Set<Integer> result = new HashSet<Integer>();
        try {
            if (scheduleJson != null && scheduleJson.length() > 0) {
                JSONArray jsonArray = new JSONArray(scheduleJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);
                    if (json.has("id")) {
                        result.add(json.getInt("id"));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    protected List<DevoxxPresentation> getScheduleData(String scheduleJson) {
        List<DevoxxPresentation> result = new ArrayList<DevoxxPresentation>();
        try {
            if (scheduleJson != null && scheduleJson.length() > 0) {
                JSONArray jsonArray = new JSONArray(scheduleJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);
                    result.add(parsePresentation(json));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // sort the schedule
        Collections.sort(result, new DevoxxPresentationComparator());

        return result;
    }

    /**
     * Parses a DevoxxPresentation object from the given JSONObject.
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    private DevoxxPresentation parsePresentation(JSONObject json)
            throws JSONException {
        DateFormat df = new SimpleDateFormat(DEVOXX_JSON_DATE_PATTERN);

        try {
            DevoxxPresentationKind kind = DevoxxPresentationKind.valueOf(json
                    .getString("kind").toUpperCase().replaceAll(" ", "_"));

            int id = 0;
            Date fromTime = df.parse(json.getString("fromTime"));
            Date toTime = df.parse(json.getString("toTime"));
            String room = json.getString("room");
            boolean partnerSlot = json.getBoolean("partnerSlot");
            String code = json.getString("code");
            String type = json.getString("type");

            String presentationUri = null;
            if (json.has("presentationUri")) {
                presentationUri = json.getString("presentationUri");

                // parse the id from the presentationUri
                id = Integer.valueOf(presentationUri.substring(presentationUri
                        .lastIndexOf("/") + 1));
            }

            String title = "TBA";
            if (kind.isSpeak()) {
                if (json.has("title")) {
                    title = json.getString("title");
                }
            } else {
                title = code;
            }

            List<DevoxxSpeaker> speakers = new ArrayList<DevoxxSpeaker>();
            if (json.has("speakers")) {
                JSONArray speakersJson = json.getJSONArray("speakers");
                for (int i = 0; i < speakersJson.length(); i++) {
                    String speakerUri = ((JSONObject) speakersJson.get(i))
                            .getString("speakerUri");
                    String speakerName = ((JSONObject) speakersJson.get(i))
                            .getString("speaker");

                    // wrap the speaker inside a lazy loading proxy
                    speakers.add(LazyLoadProxyFactory.getProxy(
                            new DevoxxSpeakerImpl(speakerName, speakerUri),
                            this));
                }
            }

            DevoxxPresentationImpl event = new DevoxxPresentationImpl(id,
                    fromTime, toTime, code, type, kind, title, speakers, room,
                    partnerSlot, presentationUri);

            // wrap the presentation inside a lazy loading proxy
            return LazyLoadProxyFactory.getProxy(event, this);
        } catch (ParseException e) {
            throw new JSONException(e);
        }
    }

    public void lazyLoadFields(LazyLoadable lazy) {
        if (lazy.getLazyLoadingUri() == null) {
            return;
        }

        try {
            logger.debug("Lazy loading object details "
                    + lazy.getLazyLoadingUri());

            JSONObject jsonData = new JSONObject(httpClient.get(
                    lazy.getLazyLoadingUri()).getResponse());

            for (Method method : lazy.getClass().getMethods()) {
                if (method.getName().startsWith("get")
                        && method.isAnnotationPresent(LazyLoad.class)) {

                    Class<?> returnType = method.getReturnType();
                    Method setterMethod = null;

                    try {
                        // find a setter for the value
                        setterMethod = lazy.getClass().getMethod(
                                "set" + method.getName().substring(3),
                                returnType);
                        String jsonField = method.getAnnotation(LazyLoad.class)
                                .value();

                        Object value = null;
                        if (jsonData.has(jsonField)) {
                            if (returnType.equals(int.class)) {
                                value = jsonData.getInt(jsonField);
                            } else {
                                // assume String
                                value = jsonData.getString(jsonField);
                            }
                        } else {
                            logger.warn("No field found for name " + jsonField);
                        }

                        // call the setter
                        setterMethod.invoke(lazy, value);
                    } catch (NoSuchMethodException e) {
                        logger.error("No matching setter found for getter: "
                                + method.getName());
                    } catch (IllegalArgumentException e) {
                        logger.error("Illegal argument for setter "
                                + setterMethod.getName() + ": "
                                + e.getMessage());
                    } catch (IllegalAccessException e) {
                        logger.error("Illegal access to setter "
                                + setterMethod.getName() + ": "
                                + e.getMessage());
                    } catch (InvocationTargetException e) {
                        logger.error("Couldn't invoke setter "
                                + setterMethod.getName() + ": "
                                + e.getMessage());
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

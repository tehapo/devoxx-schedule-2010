package org.vaadin.devoxx2k10.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentationKind;
import org.vaadin.devoxx2k10.data.domain.DevoxxSpeaker;
import org.vaadin.devoxx2k10.data.json.HttpJsonDataProvider;
import org.vaadin.devoxx2k10.data.json.JsonDataProvider;

/**
 * Facade for the Devoxx REST API.
 * 
 * You can inject your own JsonDataProvider implementation by using the
 * constructor taking it as a parameter or you can use the default
 * implementation by using the no-arg constructor.
 */
public class RestApiFacade {

    private final Logger logger = Logger.getLogger(getClass());

    private final JsonDataProvider jsonProvider;

    private static final String DEVOXX_JSON_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public RestApiFacade() {
        // this(new OfflineJsonDataProvider());
        this(new HttpJsonDataProvider());
    }

    public RestApiFacade(JsonDataProvider jsonProvider) {
        logger.debug("Initializing RestApiFacade with JsonProvider "
                + jsonProvider.getClass().getName());
        this.jsonProvider = jsonProvider;
    }

    public List<DevoxxPresentation> getSchedule(Date startDate, Date endDate) {
        List<DevoxxPresentation> result = new ArrayList<DevoxxPresentation>();
        for (DevoxxPresentation event : getScheduleData()) {
            if (event.getFromTime().compareTo(startDate) >= 0
                    && event.getToTime().compareTo(endDate) <= 0) {
                result.add(event);
            }
        }
        return result;
    }

    public List<DevoxxPresentation> getFullSchedule() {
        return getScheduleData();
    }

    protected List<DevoxxPresentation> getScheduleData() {
        List<DevoxxPresentation> result = new ArrayList<DevoxxPresentation>();
        try {
            String scheduleJson = jsonProvider.getScheduleJson();

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

            Date fromTime = df.parse(json.getString("fromTime"));
            Date toTime = df.parse(json.getString("toTime"));
            String room = json.getString("room");
            int id = json.getInt("id");
            boolean partnerSlot = json.getBoolean("partnerSlot");
            String code = json.getString("code");
            String type = json.getString("type");

            String presentationUri = null;
            if (json.has("presentationUri")) {
                presentationUri = json.getString("presentationUri");
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

    /**
     * Uses reflection to fill all fields of given LazyLoadable that are
     * decorated with the LazyLoad annotation.
     * 
     * @param lazy
     */
    void lazyLoadFields(LazyLoadable lazy) {
        if (lazy.getLazyLoadingUri() == null) {
            return;
        }

        try {
            logger.debug("Lazy loading object details "
                    + lazy.getLazyLoadingUri());

            JSONObject jsonData = new JSONObject(jsonProvider.getJson(lazy
                    .getLazyLoadingUri()));

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
        }
    }

}

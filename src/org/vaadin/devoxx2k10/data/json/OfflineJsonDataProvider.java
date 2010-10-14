package org.vaadin.devoxx2k10.data.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An offline JsonDataProvider implementation for unit tests and offline
 * development.
 */
public class OfflineJsonDataProvider implements JsonDataProvider {

    private static final String TEST_JSON_SPEAKER = "{\"id\":56,\"lastName\":\"Dashorst\",\"bio\":\"Martijn Dashorst is a senior software engineer for Topicus B.V.. He is a committer on the Wicket project and works on many Wicket related projects. When he doesn't work with or on Wicket, he likes to spend time with his girlfriend and two cats.\",\"company\":\"Topicus Onderwijs\",\"talks\":[{\"title\":\"Introducing Wicket\",\"event\":\"University (3h)\",\"presentationUri\":\"http://cfp.devoxx.com/rest/v1/events/presentations/291\"}],\"imageURI\":\"http://cfp.devoxx.com/static/images/56/thumbnail.gif\",\"firstName\":\"Martijn\"}";
    private static final String TEST_JSON_PRESENTATION = "{\"summary\":\"Aspect-Oriented Programming (AOP) complements Object-Oriented Programming (OOP) by providing another way of thinking about program structure. The key unit of modularity in OOP is the class, whereas in AOP the unit of modularity is the aspect. Aspects enable the modularization of concerns such as transaction management that cut across multiple types and objects. (Such concerns are often termed crosscutting concerns in AOP literature.)\",\"id\":8,\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/16\",\"title\":\"Aspect Oriented Programing With Spring AOP\",\"speaker\":\"Jeff Brown\",\"track\":\"Java Core (SE/EE)\",\"experience\":\"SENIOR\",\"speakers\":[{\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/16\",\"speaker\":\"Jeff Brown\"}],\"type\":\"University (3h)\"}";

    public String getSpeakerJson(String speakerUri) {
        return "";
    }

    public String getPresentationJson(String presentationUri) {
        return "";
    }

    public String getJson(String jsonUri) {
        if (jsonUri.startsWith("http://cfp.devoxx.com/rest/v1/events/speakers")) {
            return TEST_JSON_SPEAKER;
        } else if (jsonUri
                .startsWith("http://cfp.devoxx.com/rest/v1/events/presentations")) {
            return TEST_JSON_PRESENTATION;
        }
        throw new RuntimeException("Unknown URI " + jsonUri);
    }

    public String getScheduleJson() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                "offline-schedule.json");
        if (is != null) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            try {
                StringBuilder jsonData = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                return jsonData.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}

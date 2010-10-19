package org.vaadin.devoxx2k10.data.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An offline HttpClient implementation for unit tests and offline development.
 */
public class OfflineHttpClientMock implements HttpClient {

    private static final String TEST_JSON_SPEAKER = "{\"id\":56,\"lastName\":\"Dashorst\",\"bio\":\"Martijn Dashorst is a senior software engineer for Topicus B.V.. He is a committer on the Wicket project and works on many Wicket related projects. When he doesn't work with or on Wicket, he likes to spend time with his girlfriend and two cats.\",\"company\":\"Topicus Onderwijs\",\"talks\":[{\"title\":\"Introducing Wicket\",\"event\":\"University (3h)\",\"presentationUri\":\"http://cfp.devoxx.com/rest/v1/events/presentations/291\"}],\"imageURI\":\"http://cfp.devoxx.com/static/images/56/thumbnail.gif\",\"firstName\":\"Martijn\"}";
    private static final String TEST_JSON_PRESENTATION = "{\"summary\":\"Aspect-Oriented Programming (AOP) complements Object-Oriented Programming (OOP) by providing another way of thinking about program structure. The key unit of modularity in OOP is the class, whereas in AOP the unit of modularity is the aspect. Aspects enable the modularization of concerns such as transaction management that cut across multiple types and objects. (Such concerns are often termed crosscutting concerns in AOP literature.)\",\"id\":8,\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/16\",\"title\":\"Aspect Oriented Programing With Spring AOP\",\"speaker\":\"Jeff Brown\",\"track\":\"Java Core (SE/EE)\",\"experience\":\"SENIOR\",\"speakers\":[{\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/16\",\"speaker\":\"Jeff Brown\"}],\"type\":\"University (3h)\"}";
    private static final String TEST_JSON_USER_FAVOURITES = "[{\"summary\":\"Seam is a powerful open source development platform for building rich Internet applications in Java EE, now rebuilt on JSR-299: Contexts and Dependency Injection for Java EE. JSR-299 is an elegant set of new services that include dependency injection, contextual lifecycle management, configuration, interceptors and event notification. While these services are familiar, the innovative use of meta-annotations is uniquely expressive and typesafe, and a significant step forward from Seam 2. The implementation of this new programming model is provided by Weld. Seam extends the CDI programming model by providing portable enhancements, extensions and integrations that tie technologies such as Java Persistence 2 (JPA), Enterprise JavaBeans (EJB 3.1), JavaServer Faces 2 (JSF), Business Process Management (BPM), business rules (Drools), reporting (PDF and Excel), security and e-mail templates into a unified full-stack solution, supported by sophisticated tooling.\r\n\r\nIn this session, Pete Muir and Dan Allen, two of the lead Seam developers, detail the state of the union for Seam 3. We'll cover how it's being reachitected on JSR-299, cover its new modularized and autonomous infrastructure and provide an overview of features, both migrated and new. This talk has a nice blend of theory and application. Audience members will take away from this talk and understanding of CDI and Seam 3 and knowledge to get their hands dirty and started developing with this platform.\",\"id\":278,\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/244\",\"title\":\"Seam 3: State of the Union\",\"speaker\":\"Dan Allen\",\"track\":\"Web Frameworks\",\"experience\":\"SENIOR\",\"speakers\":[{\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/244\",\"speaker\":\"Dan Allen\"},{\"speakerUri\":\"http://cfp.devoxx.com/rest/v1/events/speakers/295\",\"speaker\":\"Pete Muir\"}],\"type\":\"University (3h)\"}]";

    private String getScheduleJson() {
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

    public String get(String urlString) throws IOException {
        if (urlString
                .startsWith("http://cfp.devoxx.com/rest/v1/events/speakers")) {
            return TEST_JSON_SPEAKER;
        } else if (urlString
                .startsWith("http://cfp.devoxx.com/rest/v1/events/presentations")) {
            return TEST_JSON_PRESENTATION;
        } else if (urlString
                .equals("http://cfp.devoxx.com/rest/v1/events/1/schedule")) {
            return getScheduleJson();
        } else if (urlString
                .startsWith("http://cfp.devoxx.com/rest/v1/events/1/schedule/")) {
            return TEST_JSON_USER_FAVOURITES;
        }
        throw new RuntimeException("Unknown URI " + urlString);
    }

    public int post(String urlString, String postData) throws IOException {
        return 201;
    }
}

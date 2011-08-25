package org.vaadin.devoxx2k10;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Properties configuration;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    static {
        // Load the properties file.
        configuration = new Properties();
        try {
            final InputStream propertiesStream = DevoxxScheduleApplication.class
                    .getResourceAsStream("/configuration.properties");
            if (propertiesStream != null) {
                configuration.load(propertiesStream);
            } else {
                throw new RuntimeException("Cannot read properties file: configuration.properties");
            }
        } catch (final IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getProperty(String key) {
        String property = configuration.getProperty(key);
        if (property != null) {
            property = property.trim();
        }
        return property;
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(getProperty(key));
    }

    public static String[] getArrayProperty(String key) {
        if (getProperty(key) != null) {
            String[] result = getProperty(key).split(",");
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i].trim();
            }
            return result;
        }
        return EMPTY_STRING_ARRAY;
    }

}

package org.vaadin.devoxx2k10;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Properties configuration;

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
        return configuration.getProperty(key);
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(configuration.getProperty(key));
    }

}

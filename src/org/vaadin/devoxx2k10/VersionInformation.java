package org.vaadin.devoxx2k10;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInformation {

    private static String version = "UNKNOWN_VERSION";

    static {
        // Load the application version from the properties file.
        try {
            Properties versionProperties = new Properties();
            InputStream propertiesStream = DevoxxScheduleApplication.class
                    .getClassLoader()
                    .getResourceAsStream("/version.properties");
            if (propertiesStream != null) {
                versionProperties.load(propertiesStream);
                version = versionProperties.getProperty("version");
            }
        } catch (IOException e) {
            // Shouldn't happen -> print the stack trace and just use the
            // default value.
            e.printStackTrace();
        }
    }

    public static String getVersion() {
        return version;
    }

}

package at.fhv.transflow.simulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Static class to provide some project configurations at runtime.
 * Configurations are set through the config.properties file.
 */
public abstract class AppConfig {
    private static final Properties properties = new Properties();

    public static void init(InputStream configPropertySource) throws IOException {
        if (configPropertySource == null) {
            throw new IOException("Failed to load properties! InputStream was null, i.e., stream source does not exist");
        }

        properties.load(configPropertySource);
    }

    public static Optional<String> getProperty(String propertyName) {
        return Optional.ofNullable(properties.getProperty(propertyName));
    }

    public static Optional<String[]> getPropertyList(String propertyName) {
        String propertyValue = properties.getProperty(propertyName);
        // return either an empty Optional or an Optional of the already split list
        return Optional.ofNullable(propertyValue).map(list -> list.split(","));
    }
}
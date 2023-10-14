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

    /**
     * Initialize global AppConfig with java properties from a given source (like a specific configuration file).
     * @param configPropertySource Source of the config properties in the form of an {@link InputStream}.
     * @throws IOException when the property source stream is null or the application was unable to read from it.
     */
    public static void init(InputStream configPropertySource) throws IOException {
        if (configPropertySource == null) {
            throw new IOException("Failed to load properties! InputStream was null, i.e., stream source does not exist");
        }

        properties.load(configPropertySource);
    }

    /**
     * Get the string value of a specific property from the currently loaded configuration properties.
     * @param propertyName The full name of the requested property (e.g. "admin.username").
     * @return An {@link Optional} of the value of the requested property as a string.<br>
     * The optional is empty if the property could not be found.
     */
    public static Optional<String> getProperty(String propertyName) {
        return Optional.ofNullable(properties.getProperty(propertyName));
    }

    /**
     * Get the value a specific property, which uses a comma-seperated list of strings as its value,<br>
     * from the currently loaded configuration properties.
     * @param propertyName The full name of the requested property (e.g. "admin.username").
     * @return An {@link Optional} of the value of the requested property as a list of strings.
     * The value of the property is automatically split at every comma (",") before it is returned.<br>
     * The optional is empty if the property could not be found.
     */
    public static Optional<String[]> getPropertyList(String propertyName) {
        String propertyValue = properties.getProperty(propertyName);
        // return either an empty Optional or an Optional of the already split list
        return Optional.ofNullable(propertyValue).map(list -> list.split(","));
    }

    /**
     * Get the boolean value of a specific property from the currently loaded configuration properties.
     * @param propertyName The full name of the requested property (e.g. {@code admin.username}).
     * @return An {@link Optional} of the value of the requested property as a boolean.<br>
     * The value of the property, which is inherently a string to begin with, is automatically
     * parsed to a true/false value using case-insensitive string comparison ({@code "true" -> true; "false" -> false}).<br>
     * The optional is empty if the property could not be found OR if the value of the encountered property
     * could not be parsed to a boolean.
     */
    public static Optional<Boolean> getBooleanProperty(String propertyName) {
        // parse a "true" to true, a "false" to false and everything else to an empty Optional
        return Optional.ofNullable(properties.getProperty(propertyName)).map(
            value -> {
                if ("true".equalsIgnoreCase(value)) {
                    return true;
                } else if ("false".equalsIgnoreCase(value)) {
                    return false;
                } else {
                    return null;
                }
            });
    }
}
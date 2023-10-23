package at.fhv.transflow.simulation.messaging.mqtt;

import at.fhv.transflow.simulation.messaging.MessagingException;

/**
 * Messaging exception which occurs when the connection of an MQTT client to the broker
 * was denied due to missing or incorrect authentication credentials or a lack of permissions.
 */
public class SecurityException extends MessagingException {
    public SecurityException() {
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }

    public SecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
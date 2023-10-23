package at.fhv.transflow.simulation.messaging.mqtt;

import at.fhv.transflow.simulation.messaging.MessagingException;

/**
 * Messaging exception which occurs when the communication between an MQTT client and broker
 * was abruptly aborted or could not be established at all.<br>
 * Common causes are an incorrect configuration of the connection endpoints or attributes
 * or instabilities in the communication channel.
 */
public class ConnectionException extends MessagingException {
    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

    public ConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
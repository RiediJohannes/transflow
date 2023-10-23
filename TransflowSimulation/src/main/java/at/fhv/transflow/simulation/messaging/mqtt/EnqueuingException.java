package at.fhv.transflow.simulation.messaging.mqtt;

import at.fhv.transflow.simulation.messaging.MessagingException;

/**
 * Messaging exception which occurs when an MQTT client tries to enqueue a message in its output
 * buffer (e.g. the system's memory or a file on the file system), thus failing to send the message
 * to the broker. What makes this different from the {@link ConnectionException} is that the cause
 * of the problem originates solely from the client's machine instead of an error in the communication
 * between client and broker.
 */
public class EnqueuingException extends MessagingException {
    public EnqueuingException() {
    }

    public EnqueuingException(String message) {
        super(message);
    }

    public EnqueuingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnqueuingException(Throwable cause) {
        super(cause);
    }

    public EnqueuingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
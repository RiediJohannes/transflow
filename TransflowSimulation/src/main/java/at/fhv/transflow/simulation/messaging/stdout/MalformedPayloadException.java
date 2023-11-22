package at.fhv.transflow.simulation.messaging.stdout;

import at.fhv.transflow.simulation.messaging.MessagingException;


public class MalformedPayloadException extends MessagingException {

    public MalformedPayloadException(String message, String payload) {
        super(message + "\npayload: " + payload);
    }

    public MalformedPayloadException(String message, String payload, Throwable cause) {
        super(message + "\npayload: " + payload, cause);
    }

    public MalformedPayloadException(Throwable cause) {
        super(cause);
    }
}
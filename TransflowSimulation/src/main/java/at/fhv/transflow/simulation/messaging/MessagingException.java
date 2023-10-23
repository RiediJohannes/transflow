package at.fhv.transflow.simulation.messaging;

/**
 * Abstract exception thrown by {@link IMessagingService} which comprises various exceptions that might
 * occur in the logic of the messaging service implementation. An implementation of {@link IMessagingService}
 * is therefore expected to extend this class with their own exceptions that facilitate their underlying logic.
 */
public abstract class MessagingException extends Exception {
    protected MessagingException() {
    }

    protected MessagingException(String message) {
        super(message);
    }

    protected MessagingException(String message, Throwable cause) {
        super(message, cause);
    }

    protected MessagingException(Throwable cause) {
        super(cause);
    }

    protected MessagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
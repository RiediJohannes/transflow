package at.fhv.transflow.simulation.sumo;

public class SumoConfigurationException extends Exception {

    public SumoConfigurationException() {
    }

    public SumoConfigurationException(String message) {
        super(message);
    }

    public SumoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SumoConfigurationException(Throwable cause) {
        super(cause);
    }

    public SumoConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
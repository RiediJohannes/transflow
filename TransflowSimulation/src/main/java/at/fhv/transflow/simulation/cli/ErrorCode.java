package at.fhv.transflow.simulation.cli;

public enum ErrorCode {
    INVALID_APP_CONFIG(100, "Failed to initialize application config! Please check the location and syntax of your config file."),
    NO_SIM_PATH(101, "Missing property 'sim.path' in application properties!\nThe file path of a *.sumocfg file is required to start a SUMO simulation."),
    NO_MQTT_BROKER_URL(102, "Missing property 'mqtt.brokerUrl' in application properties!\nA valid URL to an MQTT broker is required to publish simulation data."),
    NO_MQTT_CLIENT_ID(103, "Missing property 'mqtt.clientId' in application properties!\nA valid client ID for the MQTT client is required to publish simulation data to the MQTT broker."),
    NO_MQTT_ROOT_TOPIC(104, "Missing property 'mqtt.topics.root' in application properties!\nA valid root topic which all messages should go to is required to publish simulation data to the MQTT broker."),
    NO_MQTT_METRICS_TOPIC(105, "Missing property 'mqtt.topics.metrics' in application properties!\nA valid subordinate topic for simulation metrics is required to publish simulation data to the MQTT broker."),
    INVALID_CLI_ARGUMENTS(120, "Invalid command line arguments!"),
    INVALID_SIM_PATH(125, "The given path to the simulation config (*.sumocfg) is not a valid file path."),

    MESSAGING_SERVICE_UNREACHABLE(200, "Failed to establish a connection to the messaging service required to publish simulation data!"),
    EXECUTION_INTERRUPTED(500, "Execution of some program tasks were abruptly halted");


    private final int code;
    private final String message;

    ErrorCode(int errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
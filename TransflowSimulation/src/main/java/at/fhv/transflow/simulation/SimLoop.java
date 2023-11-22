package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.MessagingException;
import at.fhv.transflow.simulation.messaging.stdout.StandardOutputService;
import at.fhv.transflow.simulation.sumo.SumoController;
import at.fhv.transflow.simulation.sumo.SumoSimulation;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimLoop {

    public static void main(String[] args) {
        try {
            // load the application configuration from the local properties file
            try (InputStream props = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
                AppConfig.init(props);
            } catch (IOException exp) {
                throw new SystemError(ErrorCode.INVALID_APP_CONFIG);
            }

            // load the sumo config file and start a new sumo simulation from it
            Path simConfig = Paths.get(AppConfig.getProperty("sim.path").orElseThrow(() ->
                new SystemError(ErrorCode.NO_SIM_PATH)));

            // load MQTT configuration to open the connection to a specified MQTT broker
            String mqttBroker = AppConfig.getProperty("mqtt.brokerUrl").orElseThrow(() ->
                new SystemError(ErrorCode.NO_MQTT_BROKER_URL));
            String mqttClientId = AppConfig.getProperty("mqtt.clientId").orElseThrow(() ->
                new SystemError(ErrorCode.NO_MQTT_CLIENT_ID));
            // set some additional options for the MQTT connection
            MqttConnectionOptions mqttOptions = new MqttConnectionOptions();


            String rootTopic = AppConfig.getProperty("mqtt.topics.root").orElseThrow(() ->
                new SystemError(ErrorCode.NO_MQTT_ROOT_TOPIC));
            String metricsTopic = AppConfig.getProperty("mqtt.topics.metrics").orElseThrow(() ->
                new SystemError(ErrorCode.NO_MQTT_METRICS_TOPIC));

            try (SumoSimulation simulation = new SumoSimulation(simConfig);
//                 IMessagingService messenger = new MqttService(mqttBroker, mqttClientId, mqttOptions)) {
                 IMessagingService messenger = new StandardOutputService()) {

                // load the simulation and run it while continuously sending simulation metrics to the given messaging service
                SumoController simController = new SumoController(simulation, messenger);
                simController.runSimulation(rootTopic, metricsTopic);

            } catch (MessagingException exp) {
                throw new SystemError(ErrorCode.MESSAGING_SERVICE_UNREACHABLE,
                    "Failed to establish a connection to the specified MQTT broker! Connection URL: '" + mqttBroker + "'");
            }

        } catch (SystemError err) {
            // print the error message to the standard error output and exit the program with a pre-defined status code
            System.err.println(err.getMessage());
            System.exit(err.getSystemCode());
        } catch (Exception exp) {
            throw new RuntimeException("Unhandled exception!", exp);
        }
    }
}
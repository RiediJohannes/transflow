package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.cli.ErrorCode;
import at.fhv.transflow.simulation.cli.SimulationOptions;
import at.fhv.transflow.simulation.cli.SimulationOptionsParser;
import at.fhv.transflow.simulation.cli.SystemError;
import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.MessagingException;
import at.fhv.transflow.simulation.messaging.mqtt.MqttService;
import at.fhv.transflow.simulation.messaging.stdout.StandardOutputService;
import at.fhv.transflow.simulation.sumo.SumoConfigurationException;
import at.fhv.transflow.simulation.sumo.SumoController;
import at.fhv.transflow.simulation.sumo.SumoSimulation;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;


public class RunSim {

    public static void main(String[] args) {
        try {
            // parse the command-line options given by the user - use defaults if absent
            var argParser = new SimulationOptionsParser("transflow-sim [sumo-config-path]");
            SimulationOptions options = argParser.parse(args);

            // load the application configuration from the local properties file
            try (InputStream props = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
                AppConfig.init(props);
            } catch (IOException exp) {
                throw new SystemError(ErrorCode.INVALID_APP_CONFIG);
            }

            if (options.getSimConfigPath() == null) {
                // look for the simulation config file path in the application's properties
                options.setSimConfigPath(Paths.get(AppConfig.getProperty("sim.path").orElseThrow(() ->
                    new SystemError(ErrorCode.NO_SIM_PATH))));
            }

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


            try (SumoSimulation simulation = new SumoSimulation(
                options.getSimConfigPath(), options.getStepIncrement(), options.getStepMillis());

                 IMessagingService messenger = new MqttService(mqttBroker, mqttClientId, mqttOptions)) {
//                 IMessagingService messenger = new StandardOutputService(false)) {

                // load the simulation and run it while continuously sending simulation metrics to the given messaging service
                SumoController simController = new SumoController(simulation, messenger);
                simController.runSimulation(rootTopic, metricsTopic, options.getDelayMillis());

            } catch (MessagingException exp) {
                throw new SystemError(ErrorCode.MESSAGING_SERVICE_UNREACHABLE,
                    "Failed to establish a connection to the specified MQTT broker! Connection URL: '" + mqttBroker + "'");
            } catch (SumoConfigurationException exp) {
                throw new SystemError(ErrorCode.INVALID_CLI_ARGUMENTS, exp.getMessage());
            }

        } catch (SystemError err) {
            // print the error message to the standard error output and exit the program with a pre-defined status code
            System.err.println(err.getMessage());
            System.exit(err.getSystemCode());
        } catch (Exception exp) {
            throw new RuntimeException("Unhandled exception! " + exp.getMessage(), exp);
        }
    }
}
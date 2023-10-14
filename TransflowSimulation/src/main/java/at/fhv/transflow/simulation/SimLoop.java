package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.messaging.MessagingService;
import at.fhv.transflow.simulation.messaging.MqttService;
import at.fhv.transflow.simulation.sumo.SumoSimulation;
import at.fhv.transflow.simulation.sumo.SumoStep;
import at.fhv.transflow.simulation.sumo.data.VehicleData;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimLoop {

    public static void main(String[] args) {

        // load the application configuration from the local properties file
        try (InputStream props = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            AppConfig.init(props);
        } catch (IOException exp) {
            throw new RuntimeException("Failed to initialize application config!", exp);
        }

        // load the sumo config file and start a new sumo simulation from it
        Path simConfig = Paths.get(AppConfig.getProperty("sim.path").orElseThrow(() ->
            new RuntimeException("Missing property 'sim.path' in application properties!\n" +
                "The file path of a *.sumocfg file is required to start a SUMO simulation.")
        ));

        // load MQTT configuration to open the connection to a specified MQTT broker
        String mqttBroker = AppConfig.getProperty("mqtt.brokerUrl").orElseThrow(() ->
            new RuntimeException("Missing property 'mqtt.brokerUrl' in application properties!" +
                "A valid URL to an MQTT broker is required to publish simulation data.")
        );
        String mqttClientId = AppConfig.getProperty("mqtt.clientId").orElseThrow(() ->
            new RuntimeException("Missing property 'mqtt.clientId' in application properties!" +
                "A valid client ID for the MQTT client is required to publish simulation data to the MQTT broker.")
        );
        MqttConnectionOptions mqttOptions = new MqttConnectionOptions();

        try (SumoSimulation simulation = new SumoSimulation(simConfig);
            MessagingService mqtt = new MqttService(mqttBroker, mqttClientId, mqttOptions)) {

            boolean loop = true;
            for (SumoStep step : simulation) {
                System.out.println(step.getId());
                System.out.println(step.getVehicleCount());

                VehicleData[] vehicleData = step.getVehicleData();

                if (vehicleData.length > 0) {
                    System.out.println(vehicleData[0].speed());
                }

                if (loop && step.getId() == 20) {
                    simulation.skipToStep(40);
                    loop = false;
                }

                System.out.println();
            }
        } catch (Exception exp) {
            throw new RuntimeException("Unhandled exception!", exp);
        }
    }
}
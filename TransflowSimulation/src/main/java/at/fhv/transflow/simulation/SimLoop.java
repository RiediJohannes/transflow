package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.JsonMapper;
import at.fhv.transflow.simulation.messaging.MessagingException;
import at.fhv.transflow.simulation.messaging.mqtt.ConnectionException;
import at.fhv.transflow.simulation.messaging.mqtt.MqttService;
import at.fhv.transflow.simulation.sumo.SumoSimulation;
import at.fhv.transflow.simulation.sumo.SumoStep;
import at.fhv.transflow.simulation.sumo.data.VehicleData;
import com.fasterxml.jackson.core.JsonProcessingException;
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


            try (SumoSimulation simulation = new SumoSimulation(simConfig);
                 IMessagingService mqtt = new MqttService(mqttBroker, mqttClientId, mqttOptions)) {

                for (SumoStep step : simulation) {
                    System.out.println(step.getId());
                    System.out.println(step.getVehicleCount());

                    VehicleData[] vehicleData = step.getVehicleData();

                    if (vehicleData.length > 0) {
                        System.out.println(vehicleData[0].speed());

                        try {
                            String json = JsonMapper.instance().toJsonString(vehicleData);
                            VehicleData[] veh = JsonMapper.instance().fromJson(json, VehicleData[].class);
                            System.out.println(veh[0].speed() + "\n");
                            mqtt.sendMessage("sim/0/vehicles", JsonMapper.instance().toJsonBytes(vehicleData), 1);
                        } catch (JsonProcessingException exp) {
                            System.err.printf("Failed to parse objects in time step %s;\nReason: %s\n",
                                step.getId(), exp.getMessage());
                        } catch (ConnectionException exp) {
                            System.err.printf(exp.getMessage());
                        }
                    }
                }
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
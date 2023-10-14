package at.fhv.transflow.simulation.messaging;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSecurityException;

import java.io.File;

public class MqttService {

    public MqttService(String brokerUrl, String clientId) {

    }

    public MqttService(String brokerUrl, String clientId, File persistenceFile) {

    }

    public void InitializeMqtt() {
        String subTopic = "test/#/vehicles";
        String pubTopic = "test/1/vehicles";
        String content = "Hello World";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "emqx_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setUserName("emqx_test");
            options.setPassword("emqx_test_password".getBytes());
            // retain session
            options.setCleanStart(true);

            // set callback
//            client.setCallback(new OnMessageCallback());

            // establish a connection
            System.out.println("Connecting to broker: " + broker);
            client.connect(options);

            System.out.println("Connected");
            System.out.println("Publishing message: " + content);

            // Required parameters for message publishing
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            client.publish(pubTopic, message);
            System.out.println("Message published");

            client.disconnect();
            System.out.println("Disconnected");
            client.close();

        } catch (MqttSecurityException e) {
            throw new RuntimeException(e);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
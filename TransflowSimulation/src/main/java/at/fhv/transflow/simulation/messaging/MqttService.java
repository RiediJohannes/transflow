package at.fhv.transflow.simulation.messaging;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.io.File;

/**
 * An implementation of the {@link MessagingService} interface.<br>
 * Establishes a connection to a running <a href="https://mqtt.org">MQTT</a> broker (messaging provider)
 * on instantiation and provides a simplified interface to communicate with the server.<br>
 * <br>
 * MQTT-specific connection options need to be defined on instantiation of the service using the respective constructor.
 */
public class MqttService implements MessagingService {
    private final String brokerUrl;
    private final String clientId;
    private final MqttClient client;

    // private constructor to be called from multiple public constructors
    private MqttService(String brokerUrl, String clientId, MqttClientPersistence persistence, MqttConnectionOptions options) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;

        client = new MqttClient(brokerUrl, clientId, persistence);
        client.connect(options);
    }

    /**
     * Establish a new connection to a running MQTT broker with default connection options and in-memory persistence
     * of unacknowledged outbound/inbound messages.
     * @param brokerUrl A valid URL (including communication protocol) to a running MQTT broker.
     * @param clientId  The ID by which this MQTT client shall be identified.
     */
    public MqttService(String brokerUrl, String clientId) throws MqttException {
        this(brokerUrl, clientId,
            new MemoryPersistence(),
            new MqttConnectionOptions()
        );
    }

    /**
     * Establish a new connection to a running MQTT broker with default connection options and local file persistence
     * of unacknowledged outbound/inbound messages. Provide a local file to serve as client persistence.
     * @param brokerUrl       A valid URL (including communication protocol) to a running MQTT broker.
     * @param clientId        The ID by which this MQTT client shall be identified.
     * @param persistenceFile A local file to act as client persistence for unacknowledged outbound/inbound messages.
     */
    public MqttService(String brokerUrl, String clientId, File persistenceFile) throws MqttException {
        this(brokerUrl, clientId,
            new MqttDefaultFilePersistence(persistenceFile.getAbsolutePath()),
            new MqttConnectionOptions()
        );
    }

    /**
     * Establish a new connection to a running MQTT broker with custom connection options and in-memory persistence
     * of unacknowledged outbound/inbound messages.
     * @param brokerUrl A valid URL (including communication protocol) to a running MQTT broker.
     * @param clientId  The ID by which this MQTT client shall be identified.
     * @param options   Custom {@link MqttConnectionOptions} to use for the connection to the MQTT broker.
     */
    public MqttService(String brokerUrl, String clientId, MqttConnectionOptions options) throws MqttException {
        this(brokerUrl, clientId,
            new MemoryPersistence(),
            options
        );
    }

    /**
     * Establish a new connection to a running MQTT broker with custom connection options and local file persistence
     * of unacknowledged outbound/inbound messages. Provide a local file to serve as client persistence.
     * @param brokerUrl       A valid URL (including communication protocol) to a running MQTT broker.
     * @param clientId        The ID by which this MQTT client shall be identified.
     * @param persistenceFile A local file to act as client persistence for unacknowledged outbound/inbound messages.
     * @param options         Custom {@link MqttConnectionOptions} to use for the connection to the MQTT broker.
     */
    public MqttService(String brokerUrl, String clientId, File persistenceFile, MqttConnectionOptions options) throws MqttException {
        this(brokerUrl, clientId,
            new MqttDefaultFilePersistence(persistenceFile.getAbsolutePath()),
            options
        );
    }


    @Override
    public void sendMessage(String topic, byte[] payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        client.publish(topic, message);
    }

    @Override
    public void close() throws Exception {
        client.disconnect();
        client.close();
    }
}
package at.fhv.transflow.simulation.messaging.mqtt;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.MessagingException;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.MqttSecurityException;

import java.io.File;

/**
 * An implementation of the {@link IMessagingService} interface.<br>
 * Establishes a connection to a running <a href="https://mqtt.org">MQTT</a> broker (messaging provider)
 * on instantiation and provides a simplified interface to communicate with the server.<br>
 * <br>
 * MQTT-specific connection options need to be defined on instantiation of the service using the respective constructor.
 */
public class MqttService implements IMessagingService {
    private final String brokerUrl;
    private final String clientId;
    private final MqttClient client;

    // private constructor to be called from multiple public constructors
    private MqttService(String brokerUrl, String clientId, MqttClientPersistence persistence, MqttConnectionOptions options) throws MessagingException {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;

        try {
            client = new MqttClient(brokerUrl, clientId, persistence);
            client.connect(options);
        } catch (MqttSecurityException exp) {
            throw new SecurityException("Failed to connect to MQTT broker! Client could not be authorized.", exp);
        } catch (MqttException exp) {
            throw new EnqueuingException("Failed to connect to MQTT broker! Server could not be reached.", exp);
        }
    }

    /**
     * Establish a new connection to a running MQTT broker with default connection options and in-memory persistence
     * of unacknowledged outbound/inbound messages.
     * @param brokerUrl A valid URL (including communication protocol) to a running MQTT broker.
     * @param clientId  The ID by which this MQTT client shall be identified.
     */
    public MqttService(String brokerUrl, String clientId) throws MessagingException {
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
    public MqttService(String brokerUrl, String clientId, File persistenceFile) throws MessagingException {
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
    public MqttService(String brokerUrl, String clientId, MqttConnectionOptions options) throws MessagingException {
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
    public MqttService(String brokerUrl, String clientId, File persistenceFile, MqttConnectionOptions options) throws MessagingException {
        this(brokerUrl, clientId,
            new MqttDefaultFilePersistence(persistenceFile.getAbsolutePath()),
            options
        );
    }


    @Override
    public void sendMessage(String topic, byte[] payload, int qos) throws EnqueuingException, ConnectionException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        try {
            client.publish(topic, message);
        } catch (MqttPersistenceException exp) {
            throw new EnqueuingException("Failed to enqueue message with ID " + message.getId() + " in the chosen " +
                "method of output buffer (e.g. in-memory storage or file-based storage).", exp);
        } catch (MqttException exp) {
            throw new ConnectionException("Failed to send message with ID " + message.getId() + " to broker.", exp);
        }
    }

    @Override
    public void close() throws MessagingException {
        try {
            client.disconnect();
            client.close();
        } catch (MqttException exp) {
            throw new ConnectionException("Failed to gracefully disconnect from MQTT broker.", exp);
        }
    }
}
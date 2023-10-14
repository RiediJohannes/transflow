package at.fhv.transflow.simulation.messaging;

import org.eclipse.paho.mqttv5.common.MqttException;

/**
 * Provides a common interface to communicate with a messaging service of choice.
 * The implementation of this interface is expected to provide a connection to a messaging
 * provider with the implementation-specific required configuration parameters.<br>
 * <br>
 * Use this class within a try-with-resources Statement for it to securely close
 * the connection to the messaging provider after it is no longer needed.
 */
public interface MessagingService extends AutoCloseable {

    /**
     * Publishes a message over a previously established connection to the messaging provider onto the specified topic.
     * @param topic   The messaging topic to publish the message to.
     * @param payload A byte array of the message's content.
     * @param qos     The requested quality of service for this message.
     */
    void sendMessage(String topic, byte[] payload, int qos) throws MqttException;

    @Override
    void close() throws Exception;
}
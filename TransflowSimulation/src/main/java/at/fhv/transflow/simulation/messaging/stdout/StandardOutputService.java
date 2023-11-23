package at.fhv.transflow.simulation.messaging.stdout;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.JsonMapper;
import at.fhv.transflow.simulation.messaging.MessagingException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StandardOutputService implements IMessagingService {
    private int byteCount = 0;

    @Override
    public void sendMessage(String topic, byte[] payload, int qos) throws MessagingException {
        try {
            String output = "\n[" +
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) +
                "] - Topic:" + topic +
                JsonMapper.instance().prettyPrint(new String(payload));

            System.out.println(output);
            byteCount += payload.length; // increment the counter of bytes sent
        } catch (JsonProcessingException exp) {
            throw new MalformedPayloadException("Failed to interpret bytes received as JSON!",
                new String(payload), exp);
        }
    }

    @Override
    public void close() {
        System.out.println("--- Messaging service closed ---");
        System.out.printf("Data sent: %.2f kB\n", byteCount / 1024.0);
    }
}
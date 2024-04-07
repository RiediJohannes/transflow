package at.fhv.transflow.simulation.messaging.stdout;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.JsonMapper;
import at.fhv.transflow.simulation.messaging.MessagingException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;


public class StandardOutputService implements IMessagingService {
    private static final AtomicInteger BYTE_COUNT = new AtomicInteger(0);

    @Override
    public void sendMessage(String topic, byte[] payload, int qos) throws MessagingException {
        try {
            String output = "\n[" +
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) +
                "] - Topic:" + topic +
                JsonMapper.instance().prettyPrint(new String(payload));

            String id = Pattern.compile("(?<=\"id\" : \").+(?=\")")
                .matcher(output)
                .results()
                .map(MatchResult::group)
                .toList()
                .get(0);

            System.out.println(id);
            BYTE_COUNT.addAndGet(payload.length); // increment the counter of bytes sent
        } catch (JsonProcessingException exp) {
            throw new MalformedPayloadException("Failed to interpret bytes received as JSON!",
                new String(payload), exp);
        }
    }

    @Override
    public void close() {
        System.out.println("--- Messaging service closed ---");
        System.out.printf("Data sent: %.2f kB\n", BYTE_COUNT.get() / 1024.0);
    }
}
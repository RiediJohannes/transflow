package at.fhv.transflow.simulation.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonMapper {
    private static JsonMapper instance;
    private final ObjectMapper mapper;

    private JsonMapper() {
        mapper = new ObjectMapper();

        // don't throw an exception when json has extra fields your java object does not have.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // write times as a ISO String instead of a Long to make them more human-readable.
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        // order json properties alphabetically by their keys
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // also include properties whose value is null
        mapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
    }

    public static JsonMapper instance() {
        if (instance == null) {
            instance = new JsonMapper();
        }
        return instance;
    }

    public String toJsonString(Object javaObject) throws JsonProcessingException {
        return mapper.writeValueAsString(javaObject);
    }

    public byte[] toJsonBytes(Object javaObject) throws JsonProcessingException {
        return mapper.writeValueAsBytes(javaObject);
    }

    public <T> T fromJson(String json, Class<T> valueType) throws JsonProcessingException {
        return mapper.readValue(json, valueType);
    }

    public String prettyPrint(String json) throws JsonProcessingException {
        Object jsonObject = mapper.readValue(json, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}
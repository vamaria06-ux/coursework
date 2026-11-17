package org.example.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.DataRecord;

import java.io.IOException;

public class WeatherMapper {
    private final ObjectMapper objectMapper;

    public WeatherMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DataRecord map(String json) throws  IOException {
        JsonNode root = objectMapper.readTree(json);

        JsonNode current = root.required("current");
        JsonNode currentUnits = root.required("current_units");

        String temperature = current.required("temperature_2m").asText();
        String unit = currentUnits.required("temperature_2m").asText();
        String timestamp = current.required("time").asText();

        return new DataRecord(
                "open-meteo",
                "temperature_2m",
                temperature,
                unit,
                timestamp
        );
    }
}

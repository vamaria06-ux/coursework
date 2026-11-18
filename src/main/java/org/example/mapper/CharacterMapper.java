package org.example.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CharacterMapper {

    private final ObjectMapper objectMapper;

    public CharacterMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<DataRecord> map(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode characters = root.required("results");

        if (!characters.isArray()) {
            throw new IOException("Field 'results' must be an array");
        }
        List<DataRecord> records = new ArrayList<>();
        String timestamp = Instant.now().toString();

        for (JsonNode character : characters) {
            String name = character.path("name").asText("Unknown");
            String status = character.path("status").asText("Unknown");
            String species = character.path("species").asText("Unknown");

            String origin = character.path("origin").path("name").asText("Unknown");

            DataRecord record = new DataRecord(
                    "rick-morty",
                    name,
                    status,
                    species + "; origin =" + origin,
                    timestamp
            );
            records.add(record);
        }
        return records;
    }
}

package org.example.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BookMapper {
    private final ObjectMapper objectMapper;

    public BookMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<DataRecord> map(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode books = root.required("docs");

        if (!books.isArray()) {
            throw  new IOException("Field 'docs' must be an array");
        }

        List<DataRecord> records = new ArrayList<>();
        String timestamp = Instant.now().toString();

        for (JsonNode book: books) {
            String title = book.path("title").asText("Unknown");

            JsonNode authors = book.path("author_name");
            String author = "Unknown";

            if (authors.isArray() && !authors.isEmpty()) {
                author = authors.get(0).asText();
            }

            String year = book.path("first_publish_year").asText("Unknown");

            DataRecord record = new DataRecord(
                    "open-library",
                    title + " - " + author,
                    year,
                    "year",
                    timestamp
            );
            records.add(record);
        }
        return records;
    }
}

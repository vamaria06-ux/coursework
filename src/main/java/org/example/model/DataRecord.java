package org.example.model;

public record DataRecord(
        String source,
        String name,
        String value,
        String unit,
        String timestamp
) {
}

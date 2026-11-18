package org.example.model;

import java.util.Map;
import java.util.Objects;

public record PollRequest(String sourceName, Map<String, String> parameters) {
    public PollRequest {
        Objects.requireNonNull(sourceName, "Source name is required");
        Objects.requireNonNull(parameters, "Parameters are required");

        if (sourceName.isBlank()) {
            throw new IllegalArgumentException("Source name cannot be blank");
        }
        parameters = Map.copyOf(parameters);
    }
}

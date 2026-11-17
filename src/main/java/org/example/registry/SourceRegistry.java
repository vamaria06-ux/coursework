package org.example.registry;

import org.example.source.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceRegistry {
    private final Map<String, DataSource> sources;

    public SourceRegistry(List<DataSource> dataSources) {
        Map<String, DataSource> registeredSources = new HashMap<>();

        for (DataSource source : dataSources) {
            if (registeredSources.putIfAbsent(source.getName(), source) != null) {
                throw new IllegalArgumentException("Duplicate source: " + source.getName());

            }
        }
        this.sources = Map.copyOf(registeredSources);
    }

    public DataSource getSource(String name) {
        DataSource source = sources.get(name);

        if (source == null) {
            throw new IllegalArgumentException("Unknown source: "+ name);
        }
        return source;
    }

    public List<String> getAvailableSources() {
        return sources.keySet().stream().sorted().toList();
    }
}

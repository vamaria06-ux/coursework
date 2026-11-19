package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.http.ApiHttpClient;
import org.example.mapper.BookMapper;
import org.example.mapper.CharacterMapper;
import org.example.mapper.WeatherMapper;
import org.example.registry.SourceRegistry;
import org.example.source.BookSource;
import org.example.source.CharacterSource;
import org.example.source.DataSource;
import org.example.source.WeatherSource;
import org.example.aggregation.DataAggregator;
import org.example.storage.DataStorage;
import org.example.storage.JsonStorage;
import org.example.storage.CsvStorage;
import org.example.cli.CliApplication;

import java.net.http.HttpClient;
import java.util.List;
import java.nio.file.Path;
import java.util.Locale;

public class ApplicationFactory {
    public SourceRegistry createRegistry(HttpClient httpClient) {
        ApiHttpClient apiClient = new ApiHttpClient(httpClient);
        ObjectMapper objectMapper = new ObjectMapper();

        WeatherMapper weatherMapper = new WeatherMapper(objectMapper);
        BookMapper bookMapper = new BookMapper(objectMapper);
        CharacterMapper characterMapper = new CharacterMapper(objectMapper);

        DataSource weatherSource = new WeatherSource(apiClient, weatherMapper);
        DataSource bookSource = new BookSource(apiClient, bookMapper);
        DataSource characterSource = new CharacterSource(apiClient, characterMapper);

        return new SourceRegistry(List.of(weatherSource, bookSource, characterSource));
    }

    public DataAggregator createAggregator(SourceRegistry registry) {
        return new DataAggregator(registry);
    }

    public DataStorage createJsonStorage(Path path) {
        return new JsonStorage(path, new ObjectMapper());
    }

    public DataStorage createCsvStorage(Path path) {
        return new CsvStorage(path);
    }

    public DataStorage createStorage(String format, Path path) {
        return switch (format.toLowerCase(Locale.ROOT)) {
            case "json" -> createJsonStorage(path);
            case "csv" -> createCsvStorage(path);
            default -> throw new IllegalArgumentException(
                    "Unsupported format: " + format
            );
        };
    }

    public CliApplication createCliApplication(
            SourceRegistry registry) {
        DataAggregator aggregator = createAggregator(registry);

        return new CliApplication(aggregator, this::createStorage);
    }
}

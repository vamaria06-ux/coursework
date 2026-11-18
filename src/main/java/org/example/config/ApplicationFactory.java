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

import java.net.http.HttpClient;
import java.util.List;

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
}

package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.http.ApiHttpClient;
import org.example.mapper.WeatherMapper;
import org.example.registry.SourceRegistry;
import org.example.source.DataSource;
import org.example.source.WeatherSource;

import java.net.http.HttpClient;
import java.util.List;

public class ApplicationFactory {
    public SourceRegistry createRegistry(HttpClient httpClient) {
        ApiHttpClient apiClient = new ApiHttpClient(httpClient);
        ObjectMapper objectMapper = new ObjectMapper();
        WeatherMapper weatherMapper = new WeatherMapper(objectMapper);
        DataSource weatherSource = new WeatherSource(apiClient, weatherMapper);

        return new SourceRegistry(List.of(weatherSource));
    }
}

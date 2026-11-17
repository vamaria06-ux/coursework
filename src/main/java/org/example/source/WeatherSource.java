package org.example.source;

import org.example.http.ApiHttpClient;
import org.example.mapper.WeatherMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class WeatherSource implements DataSource{

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    private final ApiHttpClient apiClient;
    private final WeatherMapper weatherMapper;

    public WeatherSource(ApiHttpClient apiClient, WeatherMapper weatherMapper) {
        this.apiClient = apiClient;
        this.weatherMapper = weatherMapper;
    }

    @Override
    public String getName() {
        return "open-meteo";
    }

    @Override
    public List<DataRecord> fetch(Map<String, String> parameters)
        throws IOException, InterruptedException {
        String latitude = parameters.get("latitude");
        String longitude = parameters.get("longitude");

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }
        String url = BASE_URL
                + "?latitude=" + encode(latitude)
                + "&longitude=" + encode(longitude)
                + "&current=temperature_2m";

        String json = apiClient.get(url);

        DataRecord record = weatherMapper.map(json);

        return List.of(record);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

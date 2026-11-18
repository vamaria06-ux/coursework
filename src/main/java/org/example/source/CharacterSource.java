package org.example.source;

import org.example.http.ApiHttpClient;
import org.example.mapper.CharacterMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CharacterSource implements  DataSource{

    private static final String BASE_URL = "https://rickandmortyapi.com/api/character/";

    private final ApiHttpClient apiClient;
    private final CharacterMapper characterMapper;

    public CharacterSource(ApiHttpClient apiClient, CharacterMapper characterMapper) {
        this.apiClient = apiClient;
        this.characterMapper = characterMapper;
    }

    @Override
    public String getName() {
        return "rick-morty";
    }

    @Override
    public List<DataRecord> fetch(Map<String,String> parameters)
        throws IOException, InterruptedException {
        String name = parameters.get("name");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Character name is required");
        }
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("?name=").append(encode(name));

        String status = parameters.get("status");

        if (status != null && !status.isBlank()) {
            url.append("&status=").append(encode(status));
        }

        String json = apiClient.get(url.toString());

        return characterMapper.map(json);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

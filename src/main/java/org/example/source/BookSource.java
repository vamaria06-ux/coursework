package org.example.source;

import org.example.http.ApiHttpClient;
import org.example.mapper.BookMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BookSource implements DataSource {
    private static final String BASE_URL = "https://openlibrary.org/search.json";

    private final ApiHttpClient apiClient;
    private final BookMapper bookMapper;

    public BookSource(ApiHttpClient apiClient, BookMapper bookMapper) {
        this.apiClient = apiClient;
        this.bookMapper = bookMapper;
    }

    @Override
    public String getName() {
        return "open-library";
    }

    @Override
    public List<DataRecord> fetch(Map<String, String> parameters)
        throws IOException, InterruptedException {
        String query = parameters.get("q");

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query is required");
        }

        String url = BASE_URL
                + "?q=" + URLEncoder.encode(
                query, StandardCharsets.UTF_8
        )
                + "&limit=3"
                + "&fields=key,title,author_name,first_publish_year,edition_count";
        String json = apiClient.get(url);
        return bookMapper.map(json);
    }
}

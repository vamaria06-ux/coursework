package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.http.ApiHttpClient;
import org.example.mapper.WeatherMapper;
import org.example.model.DataRecord;
import org.example.source.DataSource;
import org.example.source.WeatherSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class Main {
    private  static final Logger logger =  LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        try (HttpClient client = HttpClient.newHttpClient()) {
            ApiHttpClient apiClient = new ApiHttpClient((client));
            WeatherMapper weatherMapper = new WeatherMapper(new ObjectMapper());
            DataSource weatherSource = new WeatherSource(apiClient, weatherMapper);

            logger.info("Requesting weather data");

            Map<String, String> parameters = Map.of(
                    "latitude", "59.93",
                    "longitude", "30.31"
            );

            logger.info("Requesting data from {}", weatherSource.getName());

            List<DataRecord> records = weatherSource.fetch(parameters);
            logger.info("Received {} records", records.size());

            logger.info("Weather data received successfully and mapped successfully");

            for (DataRecord record : records) {
                System.out.println(record);
            }

        } catch (IOException e) {
            logger.error("Failed to fetch data", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted", e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request parameters", e);
        }
    }
}

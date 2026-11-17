package org.example;

import org.example.http.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;

public class Main {
    private  static final Logger logger =  LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=59.93"
                + "&longitude=30.31"
                + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

        try (HttpClient client = HttpClient.newHttpClient()) {
            ApiHttpClient apiClient = new ApiHttpClient((client));

            logger.info("Requesting weather data");

            String json = apiClient.get(url);
            logger.info("Weather data received successfully");
            System.out.println(json);
        } catch (IOException e) {
            logger.error("Failed to request weather data", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Weather request interrupted", e);
        }
    }
}

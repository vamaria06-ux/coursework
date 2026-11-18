package org.example;

import org.example.config.ApplicationFactory;
import org.example.model.DataRecord;
import org.example.registry.SourceRegistry;
import org.example.source.DataSource;
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
            ApplicationFactory factory = new ApplicationFactory();
            SourceRegistry registry = factory.createRegistry(client);

            logger.info("Available sources: {}", registry.getAvailableSources());

            DataSource source = registry.getSource("rick-morty");

            Map<String, String> parameters = Map.of(
                    "name", "morty"
            );

            logger.info("Requesting data from {}", source.getName());

            List<DataRecord> records = source.fetch(parameters);
            logger.info("Received {} records", records.size());

            for (DataRecord record : records) {
                System.out.println(record);
            }

        } catch (IOException e) {
            logger.error("Failed to fetch data", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted", e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid source or parameters", e);
        }
    }
}

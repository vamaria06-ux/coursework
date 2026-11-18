package org.example;

import org.example.aggregation.DataAggregator;
import org.example.config.ApplicationFactory;
import org.example.model.DataRecord;
import org.example.model.PollRequest;
import org.example.registry.SourceRegistry;
import org.example.storage.DataStorage;
import org.example.storage.WriteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

public class Main {
    private  static final Logger logger =  LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        try (HttpClient client = HttpClient.newHttpClient()) {
            ApplicationFactory factory = new ApplicationFactory();
            SourceRegistry registry = factory.createRegistry(client);
            DataAggregator aggregator = factory.createAggregator(registry);

            List<PollRequest> requests = List.of(
                    new PollRequest(
                            "open-meteo",
                            Map.of(
                                    "latitude", "59.93",
                                    "longitude", "30.31"
                            )
                    ),
                    new PollRequest(
                            "open-library",
                            Map.of("q", "harry potter")
                    ),
                    new PollRequest(
                            "rick-morty",
                            Map.of(
                                    "name", "morty",
                                    "status", "alive"
                            )
                    )
            );

            logger.info("Starting aggregation");

            List<DataRecord> records = aggregator.aggregate(requests);

            logger.info("Aggregation completed: {} records", records.size());

            DataStorage storage = factory.createJsonStorage(Path.of("data", "result.json"));

            storage.save(records, WriteMode.OVERWRITE);
            logger.info("Saved {} records to JSON", records.size());

            List<DataRecord> savedRecords = storage.readAll();
            logger.info("Read {} records from JSON", savedRecords.size());

            for (DataRecord record : savedRecords) {
                System.out.println(record);
            }

        } catch (IOException e) {
            logger.error("Aggregation failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Aggregation interrupted", e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid aggregation  parameters", e);
        }
    }
}

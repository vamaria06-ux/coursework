package org.example.cli;

import org.example.aggregation.DataAggregator;
import org.example.model.DataRecord;
import org.example.model.PollRequest;
import org.example.storage.DataStorage;
import org.example.storage.WriteMode;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;


public class CliApplication {
    private final DataAggregator aggregator;
    private final BiFunction<String, Path, DataStorage> storageFactory;

    public CliApplication(DataAggregator aggregator,
                          BiFunction<String, Path, DataStorage> storageFactory) {
        this.aggregator = aggregator;
        this.storageFactory = storageFactory;
    }

    public void run(String[] args)
        throws IOException, InterruptedException {
        if (args.length == 0) {
            throw  new IllegalArgumentException("Command is required: fetch or read");
        }
        String command = args[0];
        Map<String, List<String>> options = parseOptions(args);

        switch (command) {
            case "fetch" -> fetch(options);
            case "read" -> read(options);
            default -> throw new IllegalArgumentException(
                    "Unknown command: " + command
            );
        }
    }

    private void fetch(Map<String, List<String>> options)
        throws IOException, InterruptedException {

        validateOptions(options,
                List.of("request", "format", "mode", "file"));

        String format = required(options, "format");
        String mode = required(options, "mode");
        Path path = Path.of(required(options,"file"));

        List<String> requestOptions = options.get("request");

        if (requestOptions == null || requestOptions.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one --request is requires"
            );
        }

        List<PollRequest> requests = new ArrayList<>();

        for (String request : requestOptions) {
            requests.add(parseRequest(request));
        }

        WriteMode writeMode = switch (
                mode.toLowerCase(Locale.ROOT)) {
            case "overwrite" -> WriteMode.OVERWRITE;
            case "append" -> WriteMode.APPEND;
            default -> throw new IllegalArgumentException(
                    "Unknown write mode: " + mode
            );
        };

        DataStorage storage = storageFactory.apply(format, path);
        List<DataRecord> records = aggregator.aggregate(requests);
        storage.save(records, writeMode);

        System.out.println(
                "Saved " + records.size() + " records to " + path
        );

    }

    private void read(Map<String, List<String>> options)
        throws IOException {
        validateOptions(options,
                List.of("format", "file", "source"));
        String format = required(options, "format");
        Path path = Path.of(required(options, "file"));
        String source = optional(options, "source");

        DataStorage storage = storageFactory.apply(format, path);
        List<DataRecord> records = storage.readBySource(source);

        for (DataRecord record : records) {
            System.out.println(record);
        }
        System.out.println("Total records: " + records.size());
    }

    private Map<String, List<String>> parseOptions(String[] args) {
        Map<String, List<String>> options = new HashMap<>();

        for (int i = 1; i < args.length; i+=2) {
            String key = args[i];

            if (!key.startsWith("--")
                || i + 1 >= args.length
                || args[i +1].startsWith("--")) {
                throw new IllegalArgumentException(
                        "Invalid option: " +key
                );
            }
            key = key.substring(2);
            if (options.containsKey(key)
                && !key.equals("request")) {
                throw new IllegalArgumentException( "Duplicate option: " + key);
            }
            options.computeIfAbsent(
                    key, ignored -> new ArrayList<>()
            ).add(args[i+1]);
        }
        return options;
    }

    private PollRequest parseRequest(String request) {
        String[] parts = request.split("\\?", 2);
        String sourceName = parts[0];

        Map<String, String> parameters = new HashMap<>();

        if (parts.length == 2 && !parts[1].isBlank()) {
            for (String pair : parts[1].split("&")) {
                String[] entry = pair.split("=", 2);

                if (entry.length != 2 || entry[0].isBlank()) {
                    throw new IllegalArgumentException(
                            "Invalid query parameter: " + pair
                    );
                }
                String key = URLDecoder.decode(
                        entry[0], StandardCharsets.UTF_8
                );
                String value = URLDecoder.decode(entry[1], StandardCharsets.UTF_8);

                if (parameters.putIfAbsent(key, value) != null) {
                    throw new IllegalArgumentException(
                            "Duplicate query parameter: " + key
                    );
                }
            }
        }
        return new PollRequest(sourceName, parameters);
    }

    private String required(
            Map<String, List<String>> options, String name) {
        String value = optional(options, name);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required option: --" + name
            );
        }
        return value;
    }

    private String optional(
            Map<String, List<String>> options, String name) {
        List<String> values = options.get(name);
        return values == null ? null : values.get(0);
    }

    private void validateOptions(Map<String, List<String>> options,
                                 List<String> allowed) {
        for (String name : options.keySet()) {
            if (!allowed.contains(name)) {
                throw new IllegalArgumentException(
                        "Unsupported option: --" + name
                );
            }
        }
    }

}

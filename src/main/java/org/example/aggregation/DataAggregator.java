package org.example.aggregation;

import org.example.model.DataRecord;
import org.example.model.PollRequest;
import org.example.registry.SourceRegistry;
import org.example.source.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAggregator {
    private final SourceRegistry registry;

    public DataAggregator(SourceRegistry registry) {
        this.registry = registry;
    }

    public List<DataRecord> aggregate(List<PollRequest> requests)
        throws IOException, InterruptedException {
        List<DataRecord> result = new ArrayList<>();

        for (PollRequest request : requests) {
            DataSource source = registry.getSource(request.sourceName());
            List<DataRecord> records = source.fetch(request.parameters());

            result.addAll(records);
        }
        return result;
    }
}

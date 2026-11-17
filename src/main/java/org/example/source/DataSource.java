package org.example.source;

import org.example.model.DataRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataSource {
    String getName();

    List<DataRecord> fetch(Map<String, String> parameters)
            throws IOException, InterruptedException;
}

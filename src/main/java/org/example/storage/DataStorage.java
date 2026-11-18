package org.example.storage;

import org.example.model.DataRecord;

import java.io.IOException;
import java.util.List;

public interface DataStorage {
    void save(List<DataRecord> records, WriteMode mode) throws IOException;

    List<DataRecord> readAll() throws  IOException;

    default  List<DataRecord> readBySource(String source) throws IOException {
        if (source == null) {
            return readAll();
        }
        return readAll().stream()
                .filter(record -> source.equals(record.source()))
                .toList();
    }

}

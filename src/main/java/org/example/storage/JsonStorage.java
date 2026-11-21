package org.example.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.DataRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage implements DataStorage{
    private final Path path;
    private final ObjectMapper objectMapper;

    public JsonStorage(Path path, ObjectMapper objectMapper) {
        this.path = path;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(List<DataRecord> records, WriteMode mode) throws  IOException{
        List<DataRecord> data = new ArrayList<>();

        if (mode == WriteMode.APPEND) {
            data.addAll(readAll());
        }
        data.addAll(records);

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(path.toFile(), data);
    }

    @Override
    public List<DataRecord> readAll() throws IOException {
        if (Files.notExists(path) || Files.size(path) == 0) {
            return List.of();
        }
        return objectMapper.readValue(
                path.toFile(), new TypeReference<>() {}
        );
    }


}

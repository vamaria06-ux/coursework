package org.example.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.example.model.DataRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CsvStorage implements DataStorage{
     private  static final List<String> HEADERS = List.of(
             "source", "name", "value", "unit", "timestamp"
     );

     private final Path path;

     public CsvStorage(Path path) {
         this.path = path;
     }

     @Override
     public void save(List<DataRecord> records, WriteMode mode) throws IOException {
         List<DataRecord> data = new ArrayList<>();

         if (mode == WriteMode.APPEND) {
             data.addAll(readAll());
         }
         data.addAll(records);

         Path parent = path.toAbsolutePath().getParent();

         if (parent != null) {
             Files.createDirectories(parent);
         }

         CSVFormat format = CSVFormat.DEFAULT.builder()
                 .setHeader(HEADERS.toArray(new String[0]))
                 .get();
         try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
              CSVPrinter printer = new CSVPrinter(writer, format)) {
             for (DataRecord record : data) {
                 printer.printRecord(record.source(),
                         record.name(),
                         record.value(),
                         record.unit(),
                         record.timestamp());
             }
         }
     }

     @Override
     public List<DataRecord> readAll() throws IOException {
         if (Files.notExists(path) || Files.size(path) == 0) {
             return List.of();
         }

         CSVFormat format = CSVFormat.DEFAULT.builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .get();
         List<DataRecord> records = new ArrayList<>();

         try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
              CSVParser parser = new CSVParser(reader, format)) {
             if (!parser.getHeaderNames().equals(HEADERS)) {
                 throw new IOException("Invalid CSV headers");
             }

             for (CSVRecord row : parser) {
                 if (row.size() != HEADERS.size()) {
                     throw new IOException("Invalid CSV record");
                 }

                 records.add(new DataRecord(
                         row.get("source"),
                         row.get("name"),
                         row.get("value"),
                         row.get("unit"),
                         row.get("timestamp")
                 ));
             }

         }
         return  records;
     }


}

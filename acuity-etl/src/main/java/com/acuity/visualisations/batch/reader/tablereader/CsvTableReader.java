package com.acuity.visualisations.batch.reader.tablereader;

import au.com.bytecode.opencsv.CSVReader;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsvTableReader implements TableReader, Closeable {
    private CSVReader reader;

    private List<String> columns;
    private Map<String, Integer> index = new HashMap<>();
    private long fileSize;

    @SneakyThrows
    public CsvTableReader(InputStream input, long length) {
        Reader isr = new InputStreamReader(input);

        this.reader = new CSVReader(isr);

        this.columns = Arrays.stream(reader.readNext())
                .map(String::toUpperCase)
                .map(s -> s.replace(',', ' ')).collect(Collectors.toList());

        for (int i = 0; i < columns.size(); i++) {
            index.put(columns.get(i), i);
        }
        this.fileSize = length;
    }


    @NotNull
    @Override
    public List<String> columns() {
        return columns;
    }

    @Override
    @SneakyThrows
    public Optional<String[]> nextRow(String[] columns) {
        String[] row = reader.readNext();

        if (row == null) {
            return Optional.empty();
        }

        String[] out = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i].toUpperCase().replace(',', ' ');
            if (index.containsKey(column)) {
                int idx = index.get(column);
                out[i] = row[idx] != null ? row[idx].trim() : null;
            }
        }
        return Optional.of(out);
    }

    @Override
    @SneakyThrows
    public Optional<TableRow> nextRow() {
        Object[] row = reader.readNext();

        if (row == null) {
            return Optional.empty();
        }

        return Optional.of(new CsvTableRow(row));
    }

    @Override
    @SneakyThrows
    public void close() {
        if (reader != null) {
            reader.close();
        }
    }

    private class CsvTableRow extends TableRow {
        private final Object[] row;

        CsvTableRow(Object[] row) {
            this.row = row;
        }

        public Object getValue(String column) {
            int idx = columns.indexOf(column);
            if (idx > -1 && idx < row.length) {
                return row[idx];
            }
            return null;
        }
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }
}

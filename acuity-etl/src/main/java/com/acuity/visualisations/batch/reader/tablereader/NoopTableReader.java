package com.acuity.visualisations.batch.reader.tablereader;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NoopTableReader implements TableReader {
    @NotNull
    @Override
    public List<String> columns() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String[]> nextRow(String[] columns) {
        return Optional.empty();
    }

    @Override
    public Optional<TableRow> nextRow() {
        return Optional.empty();
    }

    @Override
    public void close() {
        // default implementation ignored
    }

    @Override
    public long getFileSize() {
        return 0;
    }
}

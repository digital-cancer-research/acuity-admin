/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.batch.reader.tablereader;

import com.epam.parso.Column;
import com.epam.parso.SasFileReader;
import com.epam.parso.impl.SasFileReaderImpl;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SasTableReader implements TableReader, Closeable {
    private InputStream input;
    private SasFileReader reader;

    private List<String> columns;
    private List<Column> columnMetadata;
    private long fileSize;

    public SasTableReader(InputStream input, long length) {
        this.input = input;
        this.reader = new SasFileReaderImpl(input);
        this.columnMetadata = reader.getColumns();
        this.columns = columnMetadata.stream()
                .map(column -> column.getName().toUpperCase())
                .collect(Collectors.toList());
        this.fileSize = length;
    }


    @NotNull
    @Override
    public List<String> columns() {
        return columns;
    }

    public List<Column> getColumnMetadata() {
        return columnMetadata;
    }

    @Override
    @SneakyThrows
    public Optional<String[]> nextRow(String[] columns) {
        Object[] row = reader.readNext();

        if (row == null) {
            return Optional.empty();
        }

        String[] out = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            int idx = this.columns.indexOf(columns[i].toUpperCase());
            if (idx > -1) {
                Object value = row[idx];
                if (value == null) {
                    out[i] = "";
                } else {
                    out[i] = value.toString().trim();
                }
            }
        }
        return Optional.of(out);
    }

    @Override
    @SneakyThrows
    public Optional<TableRow> nextRow() {
        Object[] row = reader.readNext();
        if (row == null) {
            return null;
        }

        return Optional.of(new SasTableRow(row));
    }

    @Override
    @SneakyThrows
    public void close() {
        if (input != null) {
            input.close();
        }
    }

    private class SasTableRow extends TableRow {
        private final Object[] row;

        SasTableRow(Object[] row) {
            this.row = row;
        }

        public Object getValue(String column) {
            int idx = columns.indexOf(column);
            if (idx > -1) {
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

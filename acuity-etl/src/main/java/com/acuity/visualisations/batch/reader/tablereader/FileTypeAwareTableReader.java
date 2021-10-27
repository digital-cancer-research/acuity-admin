package com.acuity.visualisations.batch.reader.tablereader;

import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.data.util.FileTypeUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class FileTypeAwareTableReader implements TableReader {

    private TableReader reader;

    @SneakyThrows
    public FileTypeAwareTableReader(String location, DataProvider provider) {
        Data data = provider.get(location);

        if (FileTypeUtil.isSasFile(location)) {
            this.reader = new SasTableReader(data.stream(), data.length());
        } else if (FileTypeUtil.isCsvFile(location)) {
            this.reader = new CsvTableReader(data.stream(), data.length());
        } else {
            throw new IllegalArgumentException("Unknown file type");
        }
    }

    @NotNull
    @Override
    public List<String> columns() {
        return reader.columns();
    }

    @Override
    public Optional<String[]> nextRow(String[] columns) {
        return reader.nextRow(columns);
    }

    @Override
    public Optional<TableRow> nextRow() {
        return reader.nextRow();
    }

    @Override
    public void close() {
        reader.close();
    }

    @Override
    public long getFileSize() {
        return reader.getFileSize();
    }
}

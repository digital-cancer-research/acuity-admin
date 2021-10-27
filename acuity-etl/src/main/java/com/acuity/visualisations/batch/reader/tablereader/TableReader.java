package com.acuity.visualisations.batch.reader.tablereader;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface TableReader extends AutoCloseable {

	@NotNull
	List<String> columns();

	Optional<String[]> nextRow(String[] columns);

	Optional<TableRow> nextRow();

	void close();

	long getFileSize();
}

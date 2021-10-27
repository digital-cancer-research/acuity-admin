package com.acuity.visualisations.batch.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eobjects.metamodel.csv.CsvDataContext;
import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.delete.RowDeletionBuilder;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.query.Query;
import org.eobjects.metamodel.query.SelectItem;
import org.eobjects.metamodel.schema.Table;
import org.eobjects.metamodel.update.RowUpdationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class CsvSourceUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(CsvSourceUtil.class);

	public static List<Map<String, String>> selectRow(String fileName, final HashMap<String, List<String>> columnsToFind) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		ClassPathResource resource = new ClassPathResource(fileName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("");
		}
		DataSet dataSet = null;
		try {
			File file = resource.getFile();
			CsvDataContext context = new CsvDataContext(file);
			final Table table = context.getDefaultSchema().getTable(0);
			Query query = context.query().from(table).select(table.getColumns()).toQuery();
			dataSet = context.executeQuery(query);
			SelectItem[] selectItems = dataSet.getSelectItems();
			while (dataSet.next()) {
				int counter = 0;
				for (String columnToFind : columnsToFind.keySet()) {
					for (int i = 0; i < selectItems.length; i++) {
						SelectItem selectItem = selectItems[i];
						String value = dataSet.getRow().getValue(selectItem).toString();
						String columnName = selectItem.getColumn().getName();
						if (columnToFind.equals(columnName) && columnsToFind.get(columnToFind).contains(value)) {
							counter++;
						}
					}
				}
				if (counter != columnsToFind.size()) {
					continue;
				}
				Map<String, String> row = new HashMap<String, String>();
				for (int i = 0; i < selectItems.length; i++) {
					SelectItem selectItem = selectItems[i];
					String value = dataSet.getRow().getValue(selectItem).toString();
					String columnName = selectItem.getColumn().getName();
					row.put(columnName, value);
				}
				result.add(row);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (dataSet != null) {
				dataSet.close();
			}
		}
		return result;
	}

	public static void insertRow(String fileName, final Map<String, String> columnsToInsert) {
		ClassPathResource resource = new ClassPathResource(fileName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("");
		}
		try {
			File file = resource.getFile();
			CsvDataContext context = new CsvDataContext(file);
			final Table table = context.getDefaultSchema().getTable(0);
			context.executeUpdate(callback -> {
                RowInsertionBuilder builder = callback.insertInto(table);
                for (Map.Entry<String, String> entry : columnsToInsert.entrySet()) {
                    builder.value(entry.getKey(), entry.getValue());
                }
                builder.execute();
            });
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void deleteRow(String fileName, final HashMap<String, List<String>> columnsToFind) {
		ClassPathResource resource = new ClassPathResource(fileName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("");
		}
		try {
			File file = resource.getFile();
			CsvDataContext context = new CsvDataContext(file);
			final Table table = context.getDefaultSchema().getTable(0);
			context.executeUpdate(callback -> {
                RowDeletionBuilder builder = callback.deleteFrom(table);
                for (Map.Entry<String, List<String>> entry : columnsToFind.entrySet()) {
                    builder.where(entry.getKey()).in(entry.getValue());
                }
                builder.execute();
            });
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void modifySource(String fileName, final HashMap<String, List<String>> columnsToFind,
			final HashMap<String, Object> columnsToSet) {
		ClassPathResource resource = new ClassPathResource(fileName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("");
		}
		try {
			File file = resource.getFile();
			CsvDataContext context = new CsvDataContext(file);
			final Table table = context.getDefaultSchema().getTable(0);
			context.executeUpdate(callback -> {
                RowUpdationBuilder builder = null;
                for (Map.Entry<String, Object> entry : columnsToSet.entrySet()) {
                    if (builder == null) {
                        builder = callback.update(table).value(entry.getKey(), entry.getValue());
                    } else {
                        builder.value(entry.getKey(), entry.getValue());
                    }
                }
                for (Map.Entry<String, List<String>> entry : columnsToFind.entrySet()) {
                    builder.where(entry.getKey()).in(entry.getValue());
                }
                builder.execute();
            });
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void modifySource2(String fileName, final HashMap<String, String> columnsToFind,
			final HashMap<String, Object> columnsToSet) {
		ClassPathResource resource = new ClassPathResource(fileName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("");
		}
		try {
			File file = resource.getFile();
			CsvDataContext context = new CsvDataContext(file);
			final Table table = context.getDefaultSchema().getTable(0);
			context.executeUpdate(callback -> {
                RowUpdationBuilder builder = null;
                for (Map.Entry<String, Object> entry : columnsToSet.entrySet()) {
                    if (builder == null) {
                        builder = callback.update(table).value(entry.getKey(), entry.getValue());
                    } else {
                        builder.value(entry.getKey(), entry.getValue());
                    }
                }
                for (Map.Entry<String, String> entry : columnsToFind.entrySet()) {
                    builder.where(entry.getKey()).eq(entry.getValue());
                }
                builder.execute();
            });
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
}

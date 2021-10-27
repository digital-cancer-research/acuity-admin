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

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.batch.reader.tablereader.FileTypeAwareTableReader;
import com.acuity.visualisations.batch.reader.tablereader.SasTableReader;
import com.acuity.visualisations.batch.reader.tablereader.TableReader;
import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.data.util.FileTypeUtil;
import com.epam.parso.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class SourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    private DataProvider provider;

    public Map<String, Column> getColumnMetadata(String path) {
        if (FileTypeUtil.isSasFile(path)) {
            final Data data = provider.get(path);
            try (SasTableReader reader = new SasTableReader(data.stream(), data.length())) {
                Map<String, Column> res = new HashMap<>();
                for (Column col : reader.getColumnMetadata()) {
                    res.put(col.getName().toUpperCase(), col);
                }
                reader.close();
                return res;
            }
        } else {
            return Collections.emptyMap();
        }
    }

    public List<String> getColumnNames(String path) {
        TableReader reader = new FileTypeAwareTableReader(path, provider);
        return reader.columns();
    }

    public List<String> getDecodingValues(String path, String column) {
        try (TableReader reader = new FileTypeAwareTableReader(path, provider)) {
            String[] columnNames = new String[]{column};
            Optional<String[]> row = reader.nextRow(columnNames);
            Set<String> values = new TreeSet<>();

            while (row.isPresent()) {
                values.add(row.get()[0]);
                row = reader.nextRow(columnNames);
            }

            return new ArrayList<>(values);
        } catch (Exception readException) {
            LOGGER.error("Error reading values from file {}/{}/{}!", path, column, readException.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns set of distinct values from the given file and column.
     *
     * @param filePath
     * @param columnName
     * @return
     */
    @Cacheable(value = "adminStudyDrugsCache")
    public Set<String> getDistinctValuesFromFileForColumn(String filePath, String columnName) throws IOException {
        LOGGER.info("Reading distinct values from file {}/{}", filePath, columnName);

        try (TableReader reader = new FileTypeAwareTableReader(filePath, provider)) {
            Set<String> values = new HashSet<>();
            String[] columnNames = {columnName};
            Optional<String[]> row = reader.nextRow(columnNames);
            while (row.isPresent()) {
                values.add(row.get()[0]);
                row = reader.nextRow(columnNames);
            }
            return values;
        }
    }

    public Set<String> getCntlinDistinctLabelsFilterByKeys(String filePath, Set<String> cntlinDrugKeys) throws IOException {
        LOGGER.info("Reading distinct values from file {}", filePath);

        try (TableReader reader = new FileTypeAwareTableReader(filePath, provider)) {
                Set<String> values = new HashSet<>();
                String[] columnNames = {"FMTNAME", "LABEL"};
                Optional<String[]> row = reader.nextRow(columnNames);
                while (row.isPresent()) {
                    if (cntlinDrugKeys.contains(row.get()[0])) {
                        values.add(row.get()[1]);
                    }
                }
                return values;
        }
    }

}

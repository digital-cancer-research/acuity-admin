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

import au.com.bytecode.opencsv.CSVReader;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.web.dto.SortColumnDTO;
import com.acuity.visualisations.web.dto.TableDataDTO;
import com.acuity.visualisations.web.dto.TableMetaDTO;
import com.epam.parso.Column;
import com.epam.parso.SasFileReader;
import com.epam.parso.impl.SasFileReaderImpl;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentNavigableMap;

@Service
public class FileViewService {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final long DATA_TTL = 60 * 60 * 1000;

    private static Map<String, Long> filesLastUsage = new HashMap<>();

    private static DB db = null;

    @Value("${acuity.timezone}")
    private String timezone;

    @Autowired
    private DataProvider provider;

    @Scheduled(fixedDelay = DATA_TTL)
    public void cleanup() throws IOException {
        if (filesLastUsage.isEmpty()) {
            return;
        }

        boolean commit = false;
        createDB();

        log.info("Cleanup");
        long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<String, Long>> iterator = filesLastUsage.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Long> entry = iterator.next();
            if (now > entry.getValue() + DATA_TTL) {
                db.getTreeMap("meta").remove(entry.getKey());
                db.getTreeMap(entry.getKey()).clear();
                iterator.remove();
                log.info("Removed: {}", entry.getKey());
                commit = true;
            }
        }

        if (commit) {
            db.commit();
            db.compact();

            if (filesLastUsage.isEmpty()) {
                db.close();
                db = null;
            }
        }
    }

    private void createDB() {
        if (db == null) {
            try {
                File dbFile = File.createTempFile("mapdb", "db");
                db = DBMaker.newFileDB(dbFile)
                        .compressionEnable()
                        .transactionDisable()
                        .closeOnJvmShutdown()
                        .deleteFilesAfterClose()
                        .make();
            } catch (IOException e) {
                log.error("Error while DB create", e);
            }
        }
    }

    public TableMetaDTO load(final String fileUrl) throws IOException {
        createDB();
        ConcurrentNavigableMap<String, TableMetaDTO> filesMetaMap = db.getTreeMap("meta");
        ConcurrentNavigableMap<Integer, Object> fileDataMap = db.getTreeMap(fileUrl);

        log.debug("Load: {}", fileUrl);

        if (filesMetaMap.containsKey(fileUrl) && fileDataMap.size() > 0) {
            log.debug("Cached");
            return filesMetaMap.get(fileUrl);
        }

        InputStream is = provider.get(fileUrl).stream();

        int total = 0;
        TableMetaDTO.TableField[] fields = null;

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));

        if (fileUrl.endsWith(".sas7bdat")) {
            SasFileReader reader = new SasFileReaderImpl(is);

            List<Column> columns = reader.getColumns();
            fields = new TableMetaDTO.TableField[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                fields[i] = new TableMetaDTO.TableField(columns.get(i).getName());
            }

            total = (int) reader.getSasFileProperties().getRowCount();

            for (int i = 0; i < total; i++) {
                Object[] row = reader.readNext();
                for (int j = 0; j < row.length; j++) {
                    if (row[j] != null && row[j] instanceof Date) {
                        Date date = (Date) row[j];
                        row[j] = sdf.format(date);
                    }
                }
                fileDataMap.put(i, row);
            }

            is.close();
        } else if (fileUrl.endsWith(".csv")) {
            InputStreamReader isr = new InputStreamReader(is);
            CSVReader reader = new CSVReader(isr);

            String[] columns = reader.readNext();
            fields = new TableMetaDTO.TableField[columns.length];
            for (int i = 0; i < columns.length; i++) {
                fields[i] = new TableMetaDTO.TableField(columns[i].replaceAll("[^a-zA-Z\\d\\s-]", ""));
            }

            String[] row = reader.readNext();
            for (int i = 0; row != null; i++) {
                fileDataMap.put(i, row);
                row = reader.readNext();
            }

            total = fileDataMap.size();

            reader.close();
        }

        TableMetaDTO meta = new TableMetaDTO(total, fields);
        filesMetaMap.put(fileUrl, meta);

        db.commit();

        return meta;
    }


    public TableDataDTO read(String fileUrl, int offset, int limit, SortColumnDTO[] sortColumns) throws IOException {
        createDB();
        ConcurrentNavigableMap<String, TableMetaDTO> filesMetaMap = db.getTreeMap("meta");
        ConcurrentNavigableMap<Integer, Object> fileDataMap = db.getTreeMap(fileUrl);

        log.info("Read ({}+{}): {}", offset, limit, fileUrl);

        if (!filesMetaMap.containsKey(fileUrl) || fileDataMap.size() == 0) {
            load(fileUrl);
        }

        filesLastUsage.put(fileUrl, System.currentTimeMillis());
        List<Object[]> data = new ArrayList<>();

        if (sortColumns != null) {
            for (int i = 0; i < fileDataMap.size(); i++) {
                data.add((Object[]) fileDataMap.get(i));
            }

            Collections.sort(data, (firstRow, secondRow) -> {
                int result = 0;

                for (int i = 0; i < sortColumns.length && result == 0; i++) {
                    Object firstValue = firstRow[sortColumns[i].getColumnIndex()];
                    Object secondValue = secondRow[sortColumns[i].getColumnIndex()];

                    if (firstValue instanceof Long && secondValue instanceof Long) {
                        result = sortColumns[i].getSortOrder() * ((Long) firstValue).compareTo(((Long) secondValue));
                    } else if (firstValue instanceof Double && secondValue instanceof Double) {
                        result = sortColumns[i].getSortOrder() * ((Double) firstValue).compareTo(((Double) secondValue));
                    } else if (firstValue instanceof Date && secondValue instanceof Date) {
                        result = sortColumns[i].getSortOrder() * ((Date) firstValue).compareTo((Date) secondValue);
                    } else {
                        result = sortColumns[i].getSortOrder() * String.valueOf(firstValue).compareTo(String.valueOf(secondValue));
                    }
                }

                return result;
            });

            data = data.subList(offset, Math.min(offset + limit, data.size()));
        } else {
            for (int i = offset; i < offset + limit && fileDataMap.containsKey(i); i++) {
                data.add((Object[]) fileDataMap.get(i));
            }
        }

        return new TableDataDTO(fileDataMap.size(), data);
    }
}

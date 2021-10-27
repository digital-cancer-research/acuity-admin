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

package com.acuity.visualisations.batch.reader;

import com.acuity.visualisations.batch.holders.EOFEventHolder;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.SdtmDataHolder;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.batch.reader.tablereader.FileTypeAwareTableReader;
import com.acuity.visualisations.batch.reader.tablereader.TableReader;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.exception.MethodFailedException;
import com.acuity.visualisations.mapping.entity.FileStandard;
import com.acuity.visualisations.model.input.InputModelChunk;
import com.acuity.visualisations.model.input.InputModelChunkImpl;
import com.acuity.visualisations.report.entity.FileReport;
import com.acuity.visualisations.sdtm.SdtmData;
import com.acuity.visualisations.sdtm.SdtmDomain;
import com.acuity.visualisations.sdtm.SdtmSuppData;
import com.acuity.visualisations.service.IExecutionProfiler;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component("chunkReader")
@Scope("step")
public class InputModelChunkReader extends HoldersAware implements ResourceInfoAwareItemReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputModelChunkReader.class);

    private static final int QUERY_SIZE = 10000;

    private ResourceInfo resourceInfo;

    private Integer counter;

    private EOFEventHolder eofEventHolder;

    private ConfigurationUtil<?> configurationUtil;

    private Map<Integer, String> emptyColumns;
    private Map<Integer, String> defaultValues;

    private List<String> columnNames;

    private Set<String> columnsToSkip;

    private DataCommonReport dataCommonReport;

    private SdtmDataHolder sdtmDataHolder;

    @Autowired
    private IExecutionProfiler executionProfiler;

    @Autowired
    private DataProvider dataProvider;

    @Override
    protected void initHolders() {
        eofEventHolder = getEOFEventHolder();
        configurationUtil = getConfigurationUtil();
        dataCommonReport = getDataCommonReport();
        sdtmDataHolder = getSdtmDataHolder();
    }

    public void close() throws ItemStreamException {
        if (reader != null) {
            reader.close();
        }
    }

    private TableReader reader;

    private Set<String> processedFiles = new HashSet<>();

    private void readSdtmMainAndSuppData(SdtmDomain domain, String file) {
        if (!sdtmDataHolder.containsMainData(file)) {
            try {
                debug("Reading sdtm file: " + file);
                SdtmData data = domain.readMainFile(file, dataProvider);
                sdtmDataHolder.putMainData(file, data);
            } catch (Exception e) {
                error("Can't read sdtm file: " + file, e);
                LOGGER.error("Can't read sdtm file: {}", file, e);
            }

            if (domain.hasSupplementalData()) {
                String suppFile = SdtmDomain.getSuppFile(domain, file);
                if (!sdtmDataHolder.containsSuppData(file)) {
                    debug("Reading supp sdtm file: " + suppFile);
                    try {
                        SdtmSuppData data = domain.readSuppFile(suppFile, dataProvider);
                        sdtmDataHolder.putSuppData(file, data);
                    } catch (Exception e) {
                        error("Can't read supp sdtm file: " + suppFile, e);
                        LOGGER.error("Can't read supp sdtm file: {}", file, e);
                    }
                }
            }
        }
    }

    @SneakyThrows
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        String file = resourceInfo.toString();
        if (processedFiles.contains(file)) {
            reader = null;
            info(String.format("Skip (done) resource: %s", file));
            return;
        }
        processedFiles.add(file);

        if (FileStandard.SDTM.equals(resourceInfo.getFileStandard())) {
            SdtmDomain domain = SdtmDomain.resolveByFile(file);

            if (domain != null) {
                readSdtmMainAndSuppData(domain, file);

                switch (domain) {
                    case AE:
                        readSdtmMainAndSuppData(SdtmDomain.FA, SdtmDomain.getSiblingFile(domain, file, SdtmDomain.FA));
                        break;
                    case FA:
                        readSdtmMainAndSuppData(SdtmDomain.MH, SdtmDomain.getSiblingFile(domain, file, SdtmDomain.MH));
                        break;
                    case TR:
                        readSdtmMainAndSuppData(SdtmDomain.TU, SdtmDomain.getSiblingFile(domain, file, SdtmDomain.TU));
                        readSdtmMainAndSuppData(SdtmDomain.RS, SdtmDomain.getSiblingFile(domain, file, SdtmDomain.RS));
                        break;
                    case RS:
                        readSdtmMainAndSuppData(SdtmDomain.TU, SdtmDomain.getSiblingFile(domain, file, SdtmDomain.TU));
                        break;
                    default:
                        break;
                }

                processedFiles.add(file);
                return;
            }
        }

        try {
            info(String.format("Processing resource: %s", resourceInfo.toString()));

            counter = executionContext.getInt(resourceInfo.toString(), 0);

            columnNames = configurationUtil.getColumnNames(resourceInfo.toString());

            String resourceNameFact = resourceInfo.getResourceNameFact();

            reader = new FileTypeAwareTableReader(resourceNameFact, dataProvider);


            columnNames = validateColumns(columnNames);

            emptyColumns = configurationUtil.getEmptyColumnsDefaultValues(resourceInfo.toString(), columnsToSkip);
            defaultValues = configurationUtil.getDefaultValues(resourceInfo.toString(), columnsToSkip);
            defaultValues = configurationUtil.getDefaultValues(resourceInfo.toString());
        } catch (Exception e) {
            throw new ItemStreamException(String.format("Error opening resource %s : %s", resourceInfo.toString(), e.getMessage()), e);
        }
    }

    private List<String> validateColumns(List<String> columnNames) {

        final FileReport fileReport = dataCommonReport.getFileReport(resourceInfo.toString());

        if (reader == null) {
            columnNames.forEach(column -> fileReport.addDataSourceError(column,
                    String.format("Raw data source \"%s\" not found", resourceInfo)));

            throw new IllegalArgumentException(
                    String.format(
                            "Job Execution Id: %s, Job Name: %s, Project: %s, Study: %s. Could not find file %s.",
                            getJobExecutionId(), getJobName(), getProjectName(), getStudyName(), resourceInfo.toString()));
        }

        columnsToSkip = new HashSet<>();

        reader.columns().forEach(fileReport::setColumn);

        for (String column : columnNames) {
            fileReport.setColumnMapped(column, configurationUtil.getFieldNameByColName(resourceInfo.toString(), column),
                    configurationUtil.getEntityNameByColName(resourceInfo.toString(), column));

            if (!reader.columns().contains(column.toUpperCase())) {
                fileReport.addColumnError(column, "Raw data column " + column + " not found");

                if (configurationUtil.isUnique(column, resourceInfo.toString())) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "Job Execution Id: %s, Job Name: %s, Project: %s, Study: %s."
                                    + " Could not find column: %s in file %s. Couldn't skip this column.",
                                    getJobExecutionId(), getJobName(), getProjectName(), getStudyName(), column,
                                    resourceInfo.toString()));
                } else {
                    warn("Could not find column: " + column + " in file " + resourceInfo.toString() + ".");
                }
                columnsToSkip.add(column);
            }
        }

        List<String> validatedColumns = columnNames.stream().filter(c -> !columnsToSkip.contains(c)).collect(toList());
        return validatedColumns;
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (counter != null) {
            executionContext.putInt(resourceInfo.toString(), counter);
        }
    }

    private Set<String> sdtmProcessedFiles = new HashSet<>();

    public InputModelChunk read() throws Exception {

        if (sdtmProcessedFiles.contains(resourceInfo.toString())) {
            return null;
        }

        if (sdtmDataHolder.containsMainData(resourceInfo.toString())) {
            sdtmProcessedFiles.add(resourceInfo.toString());
            InputModelChunk sdtmDummyChunk = new InputModelChunkImpl();
            sdtmDummyChunk.setSourceName(resourceInfo.toString());
            return sdtmDummyChunk;
        }

        executionProfiler.startOperation(getJobExecutionId(), "InputModelChunkReader.read");
        try {
            return read1();
        } catch (Exception e) {
            throw new MethodFailedException(String.format("Error reading resource %s : %s", resourceInfo.toString(), e.getMessage()), e);
        } finally {
            executionProfiler.stopOperation(getJobExecutionId(), "InputModelChunkReader.read");
        }
    }

    private InputModelChunk read1() {
        if (reader == null) {
            return null;
        }
        InputModelChunk inputModelChunk = new InputModelChunkImpl();

        int itemCounter = 0;

        String[] columnNamesArray = columnNames.toArray(new String[columnNames.size()]);

        while (itemCounter < QUERY_SIZE) {

            Optional<String[]> optionalRecord = reader.nextRow(columnNamesArray);

            if (!optionalRecord.isPresent()) {
                eofEventHolder.setFileFinished(resourceInfo.toString());
                break;
            }

            String[] record = optionalRecord.get();

            itemCounter++;

            boolean isAllEmpty = true;

            for (String s : record) {
                isAllEmpty = isAllEmpty && (s == null || s.isEmpty());
            }

            if (isAllEmpty) {
                continue;
            }

            if (!emptyColumns.isEmpty()) {
                record = getExpandedRecordList(record);
            }
            if (!defaultValues.isEmpty()) {
                for (int i = 0; i < record.length; i++) {
                    if (defaultValues.containsKey(i)) {
                        if (Util.isEmpty(record[i])) {
                            record[i] = defaultValues.get(i);
                        }
                    }
                }
            }

            inputModelChunk.addRecord(counter + itemCounter, record);
        }

        if (itemCounter < QUERY_SIZE) {
            eofEventHolder.setFileFinished(resourceInfo.toString());
        }

        LOGGER.debug(String.format("Read %d rows (%d bytes) from %s", itemCounter, reader.getFileSize(), resourceInfo.toString()));

        if (itemCounter == 0) {
            return null;
        }

        counter += itemCounter;
        dataCommonReport.getFileReport(resourceInfo.toString()).setFileSize(reader.getFileSize());

        inputModelChunk.setStudyName(getStudyName());
        inputModelChunk.setProjectName(getProjectName());
        inputModelChunk.setSourceName(resourceInfo.toString());
        inputModelChunk.setColumnsToSkip(columnsToSkip);
        return inputModelChunk;
    }

    @NotNull
    private String[] getExpandedRecordList(String[] record) {
        String[] expandedRecordList = new String[record.length + emptyColumns.size()];
        int offset = 0;
        for (int i = 0; i < expandedRecordList.length; i++) {
            String rec;
            if (emptyColumns.containsKey(i)) {
                offset++;
                rec = emptyColumns.get(i);
            } else {
                rec = record[i - offset];
            }
            expandedRecordList[i] = rec;
        }
        return expandedRecordList;
    }

    @Override
    public void setResourceInfo(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }
}

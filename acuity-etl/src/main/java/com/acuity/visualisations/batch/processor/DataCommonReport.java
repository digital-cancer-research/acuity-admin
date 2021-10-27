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

package com.acuity.visualisations.batch.processor;

import com.acuity.visualisations.aspect.TimeMe;
import com.acuity.visualisations.batch.holders.JobExecutionInfoAware;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.report.dao.IDataFieldReportDao;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.dao.IDataTableReportDao;
import com.acuity.visualisations.report.dao.IDataValueReportDao;
import com.acuity.visualisations.report.entity.ColumnErrorType;
import com.acuity.visualisations.report.entity.FileReport;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.util.Pair;
import com.acuity.visualisations.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class contains functionality for writing the ETL reports. It collates and writes
 * the summary, table, field and value reports.
 */
@Component("dataCommonReport")
@Scope("prototype")
public class DataCommonReport extends JobExecutionInfoAware {

    public static final int KEEP_COUNT = 3;
    /**
     * The DAO for the summary report
     */
    @Autowired
    private IDataSummaryReportDao dataSummaryReportDao;

    /**
     * The DAO for data table report
     */
    @Autowired
    private IDataTableReportDao dataTableReportDao;

    /**
     * The DAO for data field report
     */
    @Autowired
    private IDataFieldReportDao dataFieldReportDao;

    /**
     * The DAO for data value report
     */
    @Autowired
    private IDataValueReportDao dataValueReportDao;

    /**
     * Manages access to entity classes
     */
    @Autowired
    private EntityManager entityManager;

    /**
     * A map containing a key of source file name and a value of the report for that file
     */
    private Map<String, FileReport> fileReportsMap = new HashMap<String, FileReport>();

    /**
     * Gets the file report for the given file name
     *
     * @param fileName The file name to get the report of
     * @return A {@link FileReport} object containing the report for the given file name
     */
    public FileReport getFileReport(String fileName) {
        return fileReportsMap.computeIfAbsent(fileName, s -> new FileReport());
    }

    /**
     * Publishes the summary, table, field and value reports to the database
     *
     * @param chunkContext The Spring chunk context
     */
    @TimeMe
    @Transactional
    public void publishReport(ChunkContext chunkContext) {
        debug("cleanOldReports");
        cleanOldReports();
        debug("publishReport: " + fileReportsMap.size());
        debug("publishTableReport");
        publishTableReport();
        debug("publishFieldReport");
        publishFieldReport();
        debug("publishValueReport");
        publishValueReport();
        debug("publishSummaryReport");
        publishSummaryReport(chunkContext);


        fileReportsMap.clear();
    }

    /**
     * Publishes the summary report to the database. Must be called after all other
     * reports have been published.
     *
     * @param chunkContext The Spring chunk context
     */
    private void publishSummaryReport(ChunkContext chunkContext) {
        Collection<StepExecution> stepExecutions = chunkContext.getStepContext().getStepExecution().getJobExecution().getStepExecutions();

        boolean failedExitStatus = false;
        boolean completedWithSkipsExitStatus = false;
        boolean unknownExitStatus = false;

        // Determine whether the batch job has failed or completed with skips
        for (StepExecution execution : stepExecutions) {

            if (execution.getExitStatus().getExitCode().equals("FAILED")) {
                failedExitStatus = true;
            }

            if (!failedExitStatus
                    && execution.getExitStatus().getExitCode().equals("COMPLETED_WITH_SKIPS")) {
                completedWithSkipsExitStatus = true;
            }

            if (!failedExitStatus && !completedWithSkipsExitStatus && execution.getExitStatus().getExitCode().equals("UNKNOWN")) {
                unknownExitStatus = true;
            }
        }

        // Insert the report data into the database
        publishSummaryReportData(failedExitStatus, completedWithSkipsExitStatus, unknownExitStatus);
    }

    /**
     * Publishes the summary report data to the database
     *
     * @param failedExitStatus             Whether the ETL run has a FAILED exit status
     * @param completedWithSkipsExitStatus Whether the ETL run has a COMPLETED_WITH_SKIPS exit status
     * @param unknownExitStatus            Whether the ETL run has an UNKNOWN exit status
     */
    private void publishSummaryReportData(boolean failedExitStatus, boolean completedWithSkipsExitStatus, boolean unknownExitStatus) {

        long filesSize = fileReportsMap.values().stream().mapToLong(FileReport::getFileSize).sum();
        int filesCount = (int) (fileReportsMap.values().stream().filter(r -> r.getFileSize() != 0).count());

        RagStatus ragStatus = failedExitStatus ? RagStatus.RED
                : ((completedWithSkipsExitStatus || unknownExitStatus) ? RagStatus.AMBER : RagStatus.GREEN);

        ReportSummaryEntity summary = ReportSummaryEntity.builder().jobExecID(getJobExecutionId()).studyID(getStudyName())
                .ragStatus(ragStatus).filesSize(filesSize).filesCount(filesCount).build();

        if (ragStatus != RagStatus.GREEN) {
            this.dataSummaryReportDao.insertReportDataForUnsuccessfulEtlRun(summary);
        } else {
            this.dataSummaryReportDao.insertReportDataForCompletedEtlRun(summary);
        }
    }

    /**
     * Publishes the table report to the database.
     */
    private void publishTableReport() {

        List<ReportTableEntity> data = new ArrayList<>();
        for (Map.Entry<String, FileReport> fileReportEntry : fileReportsMap.entrySet()) {

            String fileName = fileReportEntry.getKey();
            FileReport fileReport = fileReportEntry.getValue();

            // select distinct subjects in db
            Set<String> subjDB = new HashSet<>();
            if (!fileReport.getAcuityEntities().isEmpty()) {
                try {
                    for (String className : fileReport.getAcuityEntities().toArray(new String[0])) {
                        //we have entity name 'AdverseEvent' (from MAP_ENTITY) but it should be processed as 'AE'
                        if ("AdverseEvent".equals(className)) {
                            className = "AE";
                        }
                        Class<?> entityClass = ReflectionUtil.getEntityClass(className);
                        final List<String> subjectIds = entityManager.getSubjectsIdsByStudyName(entityClass, getStudyName());
                        subjDB.addAll(subjectIds);
                    }
                } catch (Exception e) {
                    error("An error has been encountered during ETL report publishing", e);
                }
            }

            data.add(ReportTableEntity.builder()
                    .jobExecID(getJobExecutionId())
                    .numSubjectsSource(fileReport.getRawSubjects().toArray().length)
                    .numSubjectsAcuity(subjDB.size())
                    .numEventRowsUploaded(fileReport.getRowsUploaded())
                    .acuityEntities(StringUtils.join(fileReport.getAcuityEntities(), ", "))
                    .fileName(fileName)
                    .studyCode(getStudyName())
                    .fileSize(fileReport.getFileSize()).build());
        }

        dataTableReportDao.insertReportData(getJobExecutionId(), data);
    }

    /**
     * Publishes the field report to the database.
     */
    private void publishFieldReport() {

        List<ReportFieldEntity> data = new ArrayList<>();

        for (Map.Entry<String, FileReport> fileReportEntry : fileReportsMap.entrySet()) {

            String fileName = fileReportEntry.getKey();
            data.addAll(publishUnmappedFields(fileName, fileReportEntry));
            for (FileReport.Column column : fileReportEntry.getValue().getColumns().values()) {
                data.addAll(publishMappedFields(fileName, column));
            }
        }

        dataFieldReportDao.insertReportData(getJobExecutionId(), data);
    }

    /**
     * Publishes unmapped fields to the report
     *
     * @param fileName        The name of the raw data file
     * @param fileReportEntry The internal ACUITY data entity
     * @return A list of {@link ReportFieldEntity} objects containing formatted report data
     */
    private List<ReportFieldEntity> publishUnmappedFields(String fileName, Map.Entry<String, FileReport> fileReportEntry) {

        List<ReportFieldEntity> data = new ArrayList<>();

        for (Map.Entry<String, List<String>> entityFields : fileReportEntry.getValue().getUnmappedEntityFields().entrySet()) {
            for (String field : entityFields.getValue()) {
                String fullFieldName = entityFields.getKey() + "." + field;
                data.add(ReportFieldEntity.builder()
                        .dataField(fullFieldName)
                        .isMapped(false)
                        .errorDescription("Not mapped")
                        .fileName(fileName)
                        .studyCode(getStudyName()).build());
            }
        }

        return data;
    }

    /**
     * Publishes mapped fields to the report
     *
     * @param fileName The name of the raw data file
     * @param column   The data column that represents the field
     * @return A list of {@link ReportFieldEntity} objects containing formatted report data
     */
    private List<ReportFieldEntity> publishMappedFields(String fileName, FileReport.Column column) {

        List<ReportFieldEntity> data = new ArrayList<>();

        if (column.isMapped()) {
            for (Pair<ColumnErrorType, String> colError : column.getColErrors()) {
                data.add(ReportFieldEntity.builder()
                        .dataField(column.getFullName())
                        .rawDataColumn(column.getRawColName())
                        .isMapped(column.isMapped())
                        .errorType(colError.getA())
                        .errorDescription(colError.getB())
                        .fileName(fileName)
                        .studyCode(getStudyName()).build());
            }

            if (column.getParseFailures() > 0) {
                boolean isError = ((double) column.getParseFailures() / column.getParsedTotal()) > 0.5;
                data.add(ReportFieldEntity.builder()
                        .dataField(column.getFullName())
                        .rawDataColumn(column.getRawColName())
                        .isMapped(column.isMapped())
                        .errorType(isError ? ColumnErrorType.DATA_ERROR : ColumnErrorType.DATA_WARNING)
                        .errorDescription((isError ? "Over 50%" : "Up to 50%") + " of source data values could not be parsed")
                        .fileName(fileName)
                        .studyCode(getStudyName()).build());
            } else if (column.getColErrors().isEmpty()) {
                data.add(ReportFieldEntity.builder()
                        .dataField(column.getFullName())
                        .rawDataColumn(column.getRawColName())
                        .isMapped(column.isMapped())
                        .errorType(ColumnErrorType.NO_ERROR)
                        .errorDescription("No errors in upload")
                        .fileName(fileName)
                        .studyCode(getStudyName()).build());
            }
        }

        return data;
    }

    /**
     * Publishes the value report to the database.
     */
    private void publishValueReport() {

        List<ReportValueEntity> data = new ArrayList<>();

        for (Map.Entry<String, FileReport> fileReportEntry : fileReportsMap.entrySet()) {
            String fileName = fileReportEntry.getKey();

            for (FileReport.Column column : fileReportEntry.getValue().getColumns().values()) {

                for (FileReport.ValueError valError : column.getValErrors()) {
                    data.add(ReportValueEntity.builder()
                            .dataField(column.getFullName())
                            .rawDataColumn(column.getRawColName())
                            .rawDataValue(valError.getRawValue())
                            .errorType(valError.getErrorType())
                            .errorDescription(valError.getErrorDesc())
                            .errorCount(column.getValueErrorCountMap().get(valError))
                            .fileName(fileName)
                            .studyCode(getStudyName())
                            .build());
                }
            }

            fileReportEntry.getValue().closeMapDB();
        }

        dataValueReportDao.insertReportData(getJobExecutionId(), data);
    }

    private void cleanOldReports() {
        dataTableReportDao.deleteOutdatedReportData(getStudyName(), KEEP_COUNT);
        dataFieldReportDao.deleteOutdatedReportData(getStudyName(), KEEP_COUNT);
        dataValueReportDao.deleteOutdatedReportData(getStudyName(), KEEP_COUNT);
    }
}

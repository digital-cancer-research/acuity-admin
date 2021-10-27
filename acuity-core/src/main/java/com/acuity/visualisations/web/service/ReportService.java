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

import com.acuity.visualisations.report.dao.IDataFieldReportDao;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.dao.IDataTableReportDao;
import com.acuity.visualisations.report.dao.IDataValueReportDao;
import com.acuity.visualisations.report.dao.IExceptionReportDao;
import com.acuity.visualisations.report.entity.ColumnErrorType;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportEntity;
import com.acuity.visualisations.report.entity.ReportExceptionEntity;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import com.acuity.visualisations.report.entity.ReportValueEntity;
import com.acuity.visualisations.web.entity.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the service layer class for ETL report data. It contains functionality that
 * will organise and format the report data so for the UI.
 */
@Service
public class ReportService implements IReportService {

    /**
     * The DAO for getting the summary report
     */
    @Autowired
    private IDataSummaryReportDao dataSummaryReportDao;

    /**
     * The DAO for the exception report
     */
    @Autowired
    private IExceptionReportDao exceptionReportDao;

    /**
     * The DAO for getting table report data
     */
    @Autowired
    private IDataTableReportDao dataTableReportDao;

    /**
     * The DAO for getting field report data
     */
    @Autowired
    private IDataFieldReportDao dataFieldReportDao;

    /**
     * The DAO for getting the value report data
     */
    @Autowired
    private IDataValueReportDao dataValueReportDao;

    @Override
    public List<ReportSummaryEntity> getSummaryData(final String studyCode) {
        return dataSummaryReportDao.selectReportData(studyCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<ReportType, List<? extends ReportEntity>> getExceptionTableFieldValueReport(final int jobExecID) {

        HashMap<ReportType, List<? extends ReportEntity>> out = new HashMap<ReportType, List<? extends ReportEntity>>();

        out.put(ReportType.EXCEPTION, getExceptionReport(jobExecID));
        out.put(ReportType.TABLE, getTableReport(jobExecID));
        out.put(ReportType.FIELD, getFieldReport(jobExecID));
        out.put(ReportType.VALUE, getValueReport(jobExecID));

        return out;
    }

    /**
     * Gets the exception report data
     *
     * @param jobExecID The job execution ID to get the report data for
     * @return A list of {@link ReportExceptionEntity} objects containing the report data
     */
    private List<ReportExceptionEntity> getExceptionReport(int jobExecID) {
        return this.exceptionReportDao.selectReportData(jobExecID);
    }

    /**
     * Creates the ACUITY table report
     *
     * @param jobExecID The batch job execution ID to create the report for
     * @return A list of {@link ReportTableEntity} objects containing the report data
     */
    private List<ReportTableEntity> getTableReport(int jobExecID) {
        return dataTableReportDao.selectReportData(jobExecID);
    }

    /**
     * Creates the field report
     *
     * @param jobExecID The batch job execution ID to create the report for
     * @return A list of {@link ReportFieldEntity} objects containing the report data
     */
    private List<ReportFieldEntity> getFieldReport(int jobExecID) {

        List<ReportFieldEntity> data = this.dataFieldReportDao.selectReportData(jobExecID);

        for (ReportFieldEntity row : data) {

            // Decide on the most appropriate RAG status if there is a warning or error and the column is unmapped
            formatMultipleFieldError(row);

            // Format the error string
            formatFieldErrorType(row);
        }

        return data;
    }

    /**
     * Creates the value report
     *
     * @param jobExecID The batch job execution ID to create the report for
     * @return A list of {@link ReportValueEntity} objects containing the report data
     */
    private List<ReportValueEntity> getValueReport(int jobExecID) {

        List<ReportValueEntity> data = this.dataValueReportDao.selectReportData(jobExecID);

        // Format the error string
        for (ReportValueEntity row : data) {
            formatValueErrorType(row);
        }

        return data;
    }

    /**
     * If there is a warning or error and the column is unmapped, then this method
     * will assign the most appropriate RAG status and description. Only to be used
     * when constructing field reports.
     *
     * @param row The {@link ReportFieldEntity} to edit
     */
    private void formatMultipleFieldError(ReportFieldEntity row) {
        if ((row.getErrorType() == ColumnErrorType.DATA_WARNING || row.getErrorType() == ColumnErrorType.DATA_ERROR) && !row.isMapped()) {

            row.setErrorDescription(row.getErrorDescription() + ". No mapping has been found for this column");

            if (row.getErrorType() == ColumnErrorType.DATA_ERROR) {
                row.setRagStatus(RagStatus.RED);
            } else {
                row.setRagStatus(RagStatus.AMBER);
            }
        }
    }

    /**
     * Appropriately formats the error type in the field report row
     *
     * @param row The row to edit
     */
    private void formatFieldErrorType(ReportFieldEntity row) {

        if (row.getErrorType() == null) {
            row.setErrorTypeString("-");
        } else {
            switch (row.getErrorType()) {

                case NO_ERROR:
                    row.setErrorTypeString("-");
                    break;

                case DATA_WARNING:
                case DATA_ERROR:
                    row.setErrorTypeString("Data error");
                    break;

                case DATA_SOURCE_ERROR:
                    row.setErrorTypeString("Data source error");
                    break;

                case MAPPING_ERROR:
                    row.setErrorTypeString("Mapping error");
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Appropriately formats the error type in the value report row
     *
     * @param row The row to edit
     */
    private void formatValueErrorType(ReportValueEntity row) {

        switch (row.getErrorType()) {

            case PARSE_ERROR:
                row.setErrorTypeString("Parse error");
                break;

            case PARSE_WARNING:
                row.setErrorTypeString("Parse warning");
                break;

            default:
                break;

        }
    }
}

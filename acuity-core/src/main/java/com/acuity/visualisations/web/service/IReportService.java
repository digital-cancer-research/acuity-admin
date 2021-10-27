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

import java.util.List;
import java.util.Map;

import com.acuity.visualisations.report.entity.ReportEntity;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import com.acuity.visualisations.web.entity.ReportType;

/**
 * Interface for the service layer that get ETL reports
 */
public interface IReportService {

    /**
     * Gets the summary of the report for the summary report UI table.
     * 
     * @param studyCode The study code to get the report summary data for
     * 
     * @return A list of ReportSummaryEntity objects
     */
    List<ReportSummaryEntity> getSummaryData(String studyCode);

    /**
     * Gets the data for the exception, table, field and value reports for the given batch job execution
     *  
     * @param jobExecID The job execution ID to get the data for
     * 
     * @return A map of {@link ReportType} as key and a value of {@link ReportExceptionEntity}, {@link ReportTableEntity}, 
     *         {@link ReportFieldEntity} or {@link ReportValueEntity} 
     */
    Map<ReportType, List<? extends ReportEntity>> getExceptionTableFieldValueReport(int jobExecID);
}

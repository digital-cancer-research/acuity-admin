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

package com.acuity.visualisations.report.dao;

import java.util.List;

import com.acuity.visualisations.report.entity.ReportValueEntity;

public interface IDataValueReportDao {

    /**
     * Inserts report data into the database
     *
     * @param jobExecutionId The batch job execution ID
     * @param dataTableList  The report data to insert
     */
    void insertReportData(Long jobExecutionId, List<ReportValueEntity> dataTableList);

    /**
     * Deletes report data from the database
     *
     * @param studyCode The batch job execution ID
     * @param keepCount
     */
    void deleteOutdatedReportData(String studyCode, int keepCount);

    /**
     * Gets value report data from the database
     *
     * @param jobExecID The job execution ID
     * @return A list of ReportValueEntity objects
     */
    List<ReportValueEntity> selectReportData(int jobExecID);

}

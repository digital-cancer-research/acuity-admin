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

import com.acuity.visualisations.report.entity.ReportExceptionEntity;

public interface IExceptionReportDao {

    /**
     * Inserts exception report data into the database
     *
     * @param jobExecID  The job execution ID that this report data relates to
     * @param reportData A list of {@link ReportExceptionEntity} classes containing the exception report data
     */
    void insertReportData(long jobExecID, List<ReportExceptionEntity> reportData);

    /**
     * Select exception report data from the database
     *
     * @param joBExecID The job execution ID to get the report data for
     * @return A list of {@link ReportExceptionEntity} objects containing the report data
     */
    List<ReportExceptionEntity> selectReportData(long joBExecID);

    void deleteOutdatedReportData(String studyCode, int keepCount);
}

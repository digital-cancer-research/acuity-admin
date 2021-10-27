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

import com.acuity.visualisations.report.entity.ReportTableEntity;

public interface IDataTableReportDao {

	/**
	 * Inserts report data into the database
	 * 
	 * @param jobExecutionId The batch job execution ID that the report corresponds to
	 * @param reportData  The report data to insert
	 */
	void insertReportData(Long jobExecutionId, List<ReportTableEntity> reportData);

	/**
	 * Gets table report data from the database
	 * 
	 * @param jobExecID The job execution ID
	 * 
	 * @return A list of ReportTableEntity objects
	 */
	List<ReportTableEntity> selectReportData(int jobExecID);

	void deleteOutdatedReportData(String studyCode, int keepCount);

}

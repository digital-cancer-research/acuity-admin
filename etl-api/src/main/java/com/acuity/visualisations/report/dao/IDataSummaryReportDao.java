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

import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;

public interface IDataSummaryReportDao {

	/**
	 * Inserts report data for ETL runs that have an exit status FAILED, COMPLETED_WITH_SKIPS or UNKNOWN
	 * 
	 * @param summary object to save
	 */
	void insertReportDataForUnsuccessfulEtlRun(ReportSummaryEntity summary);

	/**
	 * Inserts the summary report data into the database. The summary insert must
	 * be called after the table, field and value reports have been inserted.
	 * 
	 * @param summary object to save
	 */
	void insertReportDataForCompletedEtlRun(ReportSummaryEntity summary);

	/**
	 * Gets the RAG status of an existing record
	 * 
	 * @param jobExecID The batch job execution ID
	 * 
	 * @return The RAG status of the summary data for the batch job execution ID
	 */
	RagStatus selectRagStatus(long jobExecID);

	/**
	 * This determines whether there is summary report data present for the batch job execution
	 *  
	 * @param jobExecID The batch job execution ID
	 * 
	 * @return True if there is more than zero records present, else false.
	 */
	boolean isReportDataPresent(long jobExecID);

	/**
	 * Gets the report summary data from the database
	 * 
	 * @param studyCode The study code to get the summary data for
	 * 
	 * @return A list of ReportSummaryEntity objects for the selected studyCode
	 */
	List<ReportSummaryEntity> selectReportData(String studyCode);

	void deleteOutdatedReportData(String studyCode, int keepCount);
}

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

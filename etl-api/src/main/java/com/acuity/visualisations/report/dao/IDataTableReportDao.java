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

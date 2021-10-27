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

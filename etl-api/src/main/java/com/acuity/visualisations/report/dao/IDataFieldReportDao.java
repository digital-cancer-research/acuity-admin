package com.acuity.visualisations.report.dao;

import java.util.List;

import com.acuity.visualisations.report.entity.ReportFieldEntity;

public interface IDataFieldReportDao {

    /**
     * Inserts data into the DB.
     *
     * @param jobExecutionId The job execution ID associated with the report
     * @param reportData     The data to be inserted
     */
    void insertReportData(Long jobExecutionId, List<ReportFieldEntity> reportData);

    void deleteOutdatedReportData(String studyCode, int keepCount);

    /**
     * Gets field report data from the database
     *
     * @param jobExecID The job execution ID
     * @return A list of ReportFieldEntity objects
     */
    List<ReportFieldEntity> selectReportData(int jobExecID);

}

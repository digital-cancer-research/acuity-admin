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

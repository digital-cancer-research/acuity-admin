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

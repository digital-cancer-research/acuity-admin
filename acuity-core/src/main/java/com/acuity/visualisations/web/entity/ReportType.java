package com.acuity.visualisations.web.entity;

/**
 * Represents the type of ETL report. Either SUMMARY, TABLE, FIELD or VALUE.
 */
public enum ReportType {

    /**
     * A summary of the whole ETL report
     */
    SUMMARY,
    
    /**
     * A report on any exceptions thrown in by the system during an ETL run 
     */
    EXCEPTION,
    
    /**
     * A report on the source tables or the ACUITY data tables
     */
    TABLE,
    
    /**
     * A report on the data fields
     */
    FIELD,
    
    /**
     * A report on the data values
     */
    VALUE;
}

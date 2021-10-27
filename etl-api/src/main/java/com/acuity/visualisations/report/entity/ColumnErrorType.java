package com.acuity.visualisations.report.entity;

/**
 * A description of the error type associated with a table column
 */
public enum ColumnErrorType {
    
    /**
     * An error condition that is due to a problem with the source data 
     * not being available (e.g., network error, or data file server being 
     * unavailable which prevented the ETL from accessing the data files)
     */
    DATA_SOURCE_ERROR,
    
    /**
     * An error condition associated with the data mapping parameters 
     * (e.g., a column name in the file does not exist)
     */
    MAPPING_ERROR,
    
    /**
     * An error condition associated with the data within the file meaning 
     * that the data was not parsable for entry into the database (e.g., 
     * the data did not match the required type such as a string where an 
     * integer was expected).
     */
    DATA_ERROR,
    
    /**
     * Not strictly an error condition, where data in the raw file did 
     * not meet the desired format, so it was altered from how it appears 
     * in the source data.
     */
    DATA_WARNING,
    
    /**
     * There is no error associated with the upload
     */
    NO_ERROR
}

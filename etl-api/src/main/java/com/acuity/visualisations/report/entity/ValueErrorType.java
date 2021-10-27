package com.acuity.visualisations.report.entity;

/**
 * A description of the error type associated with a field value
 */
public enum ValueErrorType {
    
    /**
     * An error condition associated with the data within the file meaning 
     * that the data was not parsable for entry into the database (e.g., 
     * the data did not match the required type such as a string where an 
     * integer was expected).
     */
    PARSE_ERROR,
    
    /**
     * Not strictly an error condition, where data in the raw file did not 
     * meet the desired format, so it was altered from how it appears in 
     * the source data.
     */
    PARSE_WARNING
}

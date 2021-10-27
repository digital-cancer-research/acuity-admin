package com.acuity.visualisations.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bean class that represents the data that will be displayed in the
 * report field HTML table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFieldEntity implements ReportEntity {

    /**
     * The Spring batch job execution ID
     */
    private long jobExecID;
    
    /**
     * The field associated with the report
     */
    private String dataField;
    
    /**
     * The raw data column associated with the report
     */
    private String rawDataColumn;
    
    /**
     * Whether the field has been successfully mapped
     */
    private boolean isMapped;
    
    /**
     * The status of the error.
     */
    private ColumnErrorType errorType;
    
    /**
     * A string description of the error type
     */
    private String errorTypeString;
    
    /**
     * A description of the error (or warning).
     */
    private String errorDescription;
    
    /**
     * The name of the raw data file
     */
    private String fileName;
    
    /**
     * The clinical study code
     */
    private String studyCode;
    
    /**
     * The red, amber or green status
     */
    private RagStatus ragStatus;
}

package com.acuity.visualisations.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This bean represents the data that will be written to the 
 * table report HTML table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTableEntity implements ReportEntity {

    /**
     * The Spring batch job execution ID
     */
    private long jobExecID;
    
    /**
     * The number of subjects in the source file
     */
    private int numSubjectsSource;
    
    /**
     * The number of subjects in the ACUITY entities
     */
    private int numSubjectsAcuity;
    
    /**
     * The number of rows uploaded
     */
    private int numEventRowsUploaded;
    
    /**
     * The ACUITY entities that this report data belongs to
     */
    private String acuityEntities;
    
    /**
     * The source file that this report data belongs to
     */
    private String fileName;
    
    /**
     * The name of the clinical study that this report data belongs to
     */
    private String studyCode;
    
    /**
     * The error status of the row
     */
    private RagStatus ragStatus;
    
    /**
     * The number of overwritten records
     */
    private int numOverwrittenRecords;

    /**
     * Source file size in bytes
     */
    private long fileSize;
}

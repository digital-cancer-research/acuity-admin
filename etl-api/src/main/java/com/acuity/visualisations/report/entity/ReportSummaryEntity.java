package com.acuity.visualisations.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * This class encapsulates the data that will be presented in
 * the report summary table
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSummaryEntity implements ReportEntity {

    /**
     * The batch job execution ID to report on
     */
    private long jobExecID;
        
    /**
     * The clinical study ID
     */
    private String studyID;
    
    /**
     * The date the data started uploading
     */
    private Date startDate;

    /**
     * The date the data finished uploading
     */
    private Date endDate;
    
    /**
     * The duration of the data upload
     */
    private String duration;

    /**
     * The Spring batch exit code
     */
    private String exitCode;
    
    /**
     * The RAG status of the upload
     */
    private RagStatus ragStatus;
    
    /**
     * A summary of the upload
     */
    private String summary;

    /**
     * Size of all uploaded files in bytes
     */
    private long filesSize;

    /**
     * Number of files uploaded
     */
    private int filesCount;
}

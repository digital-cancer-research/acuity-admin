package com.acuity.visualisations.report.entity;

/**
 * This class encapsulates the details required for the exception report
 */
public class ReportExceptionEntity implements ReportEntity {
    
    /**
     * The job execution ID that this report relates to
     */
    private long jobExecID;
    
    /**
     * The ETL step that threw the exception
     */
    private String etlStep;
    
    /**
     * The name of the exception class
     */
    private String exceptionClass;
    
    /**
     * The stack trace of the exception
     */
    private String stackTrace;

    /**
     * The message of the exception
     */
    private String message;

    /**
     * The RAG status.
     */
    private RagStatus ragStatus;
    
    /**
     * Default constructor. Initialises a new instance of the {@link ReportExceptionEntity} class
     * without initialising the instance variables.
     */
    public ReportExceptionEntity() {
    }

    /**
     * Initialises a new instance of the ExceptionReport class by setting the instance variables
     *  
     * @param etlStep        The ETL step that threw the exception
     * @param exceptionClass The name of the exception class that was thrown
     * @param stackTrace     The stack trace of the exception
     * @param ragStatus      The RAG status.
     */
    public ReportExceptionEntity(String etlStep, String exceptionClass, String message, String stackTrace, RagStatus ragStatus) {
        this.etlStep = etlStep;
        this.exceptionClass = exceptionClass;
        this.stackTrace = stackTrace;
        this.ragStatus = ragStatus;
        this.message = message;
    }
    
    public long getJobExecID() {
        return jobExecID;
    }

    public void setJobExecID(long jobExecID) {
        this.jobExecID = jobExecID;
    }

    public void setEtlStep(String etlStep) {
        this.etlStep = etlStep;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void setRagStatus(RagStatus ragStatus) {
        this.ragStatus = ragStatus;
    }
    
    public String getEtlStep() {
        return etlStep;
    }
    
    public String getExceptionClass() {
        return exceptionClass;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
    
    public RagStatus getRagStatus() {
        return ragStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

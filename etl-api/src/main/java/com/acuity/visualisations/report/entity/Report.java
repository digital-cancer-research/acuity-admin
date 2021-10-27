package com.acuity.visualisations.report.entity;

public abstract class Report {

    private Long jobExecutionId;
    private Long primaryKey;

    public abstract String getReportFormat();

    public abstract Object[] getReportArgs();

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Long primaryKey) {
        this.primaryKey = primaryKey;
    }
}

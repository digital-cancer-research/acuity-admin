package com.acuity.visualisations.web.entity;

public class MappedColumnInfo {
    private long projectId;
    private long fileRuleId;
    private String columnName;
    private String fileRuleFilePath;

    public MappedColumnInfo() {
    }

    public MappedColumnInfo(long projectId, long fileRuleId, String columnName, String fileRuleFilePath) {
        this.projectId = projectId;
        this.fileRuleId = fileRuleId;
        this.columnName = columnName;
        this.fileRuleFilePath = fileRuleFilePath;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getFileRuleId() {
        return fileRuleId;
    }

    public void setFileRuleId(long fileRuleId) {
        this.fileRuleId = fileRuleId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFileRuleFilePath() {
        return fileRuleFilePath;
    }

    public void setFileRuleFilePath(String fileRuleFilePath) {
        this.fileRuleFilePath = fileRuleFilePath;
    }
}

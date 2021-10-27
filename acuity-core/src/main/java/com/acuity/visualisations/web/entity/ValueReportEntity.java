package com.acuity.visualisations.web.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValueReportEntity {

    private long primaryKey;
    private ReportErrorType reportErrorType;
    private String mappableDataField;
    private String rawDataSource;
    private String rawDataColumn;
    private String rawDataValue;
    private String errorStatus;
    private String errorType;
    private String errorDetail;
    
    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public ReportErrorType getReportErrorType() {
        return reportErrorType;
    }

    public void setReportErrorType(ReportErrorType reportErrorType) {
        this.reportErrorType = reportErrorType;
    }

    public String getMappableDataField() {
        return mappableDataField;
    }
    
    public void setMappableDataField(String mappableDataField) {
        this.mappableDataField = mappableDataField;
    }
    
    public String getRawDataSource() {
        return rawDataSource;
    }
    
    public void setRawDataSource(String rawDataSource) {
        this.rawDataSource = rawDataSource;
    }
    
    public String getRawDataColumn() {
        return rawDataColumn;
    }
    
    public void setRawDataColumn(String rawDataColumn) {
        this.rawDataColumn = rawDataColumn;
    }
    
    public String getRawDataValue() {
        return rawDataValue;
    }
    
    public void setRawDataValue(String rawDataValue) {
        this.rawDataValue = rawDataValue;
    }
    
    public String getErrorStatus() {
        return errorStatus;
    }
    
    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}

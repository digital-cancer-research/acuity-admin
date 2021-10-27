/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

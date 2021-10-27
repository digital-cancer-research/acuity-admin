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

package com.acuity.visualisations.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains data relating to problems with source data values from an
 * ETL run
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportValueEntity implements ReportEntity {

    /**
     * The Spring batch job execution ID
     */
    private long jobExecID;
    
    /**
     * The ACUITY data field
     */
    private String dataField;
    
    /**
     * The source column name
     */
    private String rawDataColumn;
    
    /**
     * The source data value
     */
    private String rawDataValue;
    
    /**
     * The type of error. A warning or an error.
     */
    private ValueErrorType errorType;
    
    /**
     * A text version of errorType
     */
    private String errorTypeString;
    
    /**
     * A description of the error
     */
    private String errorDescription;

    /**
     * The number of found errors
     */
    private Integer errorCount;
    
    /**
     * The name of the source file associated with the error
     */
    private String fileName;
    
    /**
     * The study code that this ETL upload relates to
     */
    private String studyCode;
    
    /**
     * The RAG status of the error
     */
    private RagStatus ragStatus;
}

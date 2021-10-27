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

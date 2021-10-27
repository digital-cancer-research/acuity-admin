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

/**
 * Represents the type of ETL report. Either SUMMARY, TABLE, FIELD or VALUE.
 */
public enum ReportType {

    /**
     * A summary of the whole ETL report
     */
    SUMMARY,
    
    /**
     * A report on any exceptions thrown in by the system during an ETL run 
     */
    EXCEPTION,
    
    /**
     * A report on the source tables or the ACUITY data tables
     */
    TABLE,
    
    /**
     * A report on the data fields
     */
    FIELD,
    
    /**
     * A report on the data values
     */
    VALUE;
}

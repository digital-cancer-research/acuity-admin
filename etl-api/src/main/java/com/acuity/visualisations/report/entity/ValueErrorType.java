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

/**
 * A description of the error type associated with a field value
 */
public enum ValueErrorType {
    
    /**
     * An error condition associated with the data within the file meaning 
     * that the data was not parsable for entry into the database (e.g., 
     * the data did not match the required type such as a string where an 
     * integer was expected).
     */
    PARSE_ERROR,
    
    /**
     * Not strictly an error condition, where data in the raw file did not 
     * meet the desired format, so it was altered from how it appears in 
     * the source data.
     */
    PARSE_WARNING
}

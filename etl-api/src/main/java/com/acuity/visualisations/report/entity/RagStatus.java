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
import lombok.Getter;

/**
 * Enum for the error types that can be associated with the 
 * ETL report.
 */
@AllArgsConstructor
public enum RagStatus {

    /**
     * The report row contains no warnings of errors
     */
    GREEN("No errors were encountered during the loading of the data"),

    /**
     * There are warnings associated with the report row
     */
    AMBER("Completed with warnings: See upload details for more information"),

    /**
     * There are errors associated with the report row
     */
    RED("The ETL job failed due to a system error"),

    /**
     * There was a problem mapping the data
     */
    UNMAPPED("Summary data should not have an UNMAPPED status") {

        @Override
        public String getSummary() {
            throw new IllegalArgumentException(getSummary());
        }
    };

    @Getter
    private String summary;
}

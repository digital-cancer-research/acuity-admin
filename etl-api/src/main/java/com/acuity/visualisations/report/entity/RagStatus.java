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

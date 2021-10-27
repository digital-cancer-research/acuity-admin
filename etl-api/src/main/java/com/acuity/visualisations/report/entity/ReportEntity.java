package com.acuity.visualisations.report.entity;

/**
 * Marker interface to signify that an entity bean is used for reporting data
 */
public interface ReportEntity {

    /**
     * @return The Red, Amber or green status of the report
     */
    RagStatus getRagStatus();
}

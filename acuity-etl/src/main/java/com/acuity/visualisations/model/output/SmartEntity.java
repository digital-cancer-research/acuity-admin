package com.acuity.visualisations.model.output;

/**
 * This interface add some intelligence for the entity
 */
public interface SmartEntity {

    /**
     * Give this entity a chance to complete its state via calculation over existing fields
     */
    void complete();
}

package com.acuity.visualisations.model.output.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Medicine extends TimestampedEntity {

    private String studyGuid;
    private String drugName;
    private String drugParent;

}

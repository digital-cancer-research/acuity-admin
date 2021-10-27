package com.acuity.visualisations.model.output.entities;

import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Pathology extends TimestampedEntity {

    protected String part;
    protected String subject;

    private String patientGuid;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    protected LocalDateTime date;

    private String histologyType;
    private String histologyTypeDetails;
    private String tumourGrade;
    private String stage;
    private String tumorLocation;
    private String primaryTumourStatus;
    private String nodesStatus;
    private String metastasesStatus;
    private String methodOfDetermination;
    private String otherMethods;
}

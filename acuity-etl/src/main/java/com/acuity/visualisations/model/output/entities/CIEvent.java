package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CIEvent extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    private String eventTerm;
    private Integer aeNumber;
    private String ischemicSymptoms;
    private String cieSymptomsDuration;
    private String symptCausedUnscheduledHosp;
    private String symptCausedStentThromb;
    private String prevEcgBeforeAvailableEvent;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime prevEcgDate;
    private String availableEcgAtEventTime;
    private String noEcgAtEventTime;
    private String localCardiacBiomarkersDrawn;
    private String coronaryAngiographyPerformed;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime angiographyDate;
    private String finalDiagnosis;
    private String otherDiagnosis;
    private String description1;
    private String description2;
    private String description3;
    private String description4;
    private String description5;
}

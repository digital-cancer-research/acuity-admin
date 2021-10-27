package com.acuity.visualisations.model.output.entities;

import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeriousAdverseEvent extends TimestampedEntity {

    private String subject;
    private String part;
    private String patientGuid;

    private String aeText;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime aeBecomeSeriousDate;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime aeFindOutDate;

    private String isResultInDeath;
    private String isLifeThreatening;
    private String isHospitalizationRequired;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime hospitalizationDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime dischargeDate;

    private String isDisability;
    private String isCongenitalAnomaly;
    private String isOtherSeriousEvent;
    private String isCausedByOther;
    private String otherMedication;
    private String isCausedByStudy;
    private String studyProcedure;
    private String aeDescription;
    private Integer aeNum;
    private String primaryCauseOfDeath;
    private String secondaryCauseOfDeath;
    private String additionalDrug;
    private String causedByAdditionalDrug;
    private String additionalDrug1;
    private String causedByAdditionalDrug1;
    private String additionalDrug2;
    private String causedByAdditionalDrug2;
}

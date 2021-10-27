package com.acuity.visualisations.model.output.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EG extends TimestampedEntity {

    private String testGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    private String testName;
    private BigDecimal testResult;
    private String resultUnit;

    private String evaluation;
    private String abnormality;
    private String significant;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime dateOfLastDose;
    private String lastDoseAmount;
    private String method;
    private String atrialFibrillation;
    private String sinusRhythm;
    private String reasonNoSinusRhythm;
    private String heartRhythm;
    private String heartRhythmOther;
    private String extraSystoles;
    private String specifyExtraSystoles;
    private String typeOfConduction;
    private String conduction;
    private String reasonAbnormalConduction;
    private String sttChanges;
    private String stSegment;
    private String tWave;

    private String protocolScheduleTimePoint;

    public EG(String subject, String part, LocalDateTime date, String testName, BigDecimal testResult, String resultUnit,
              String evaluation, String abnormality, String significant) {
        this.subject = subject;
        this.part = part;
        this.date = date;
        this.testName = testName;
        this.testResult = testResult;
        this.resultUnit = resultUnit;
        this.evaluation = evaluation;
        this.abnormality = abnormality;
        this.significant = significant;
    }
}

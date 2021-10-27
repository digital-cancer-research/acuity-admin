package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConcomitantMedSchedule extends TimestampedEntity {

    private String patientGuid;
    private String medicineGuid;
    private BigDecimal dose;
    private String doseUnit;
    private String frequency;
    private String atcCode;
    private String atcCodeText;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;

    private String treatmentReason; // indication on ui
    private String subject;
    private String part;
    private String drugName;
    private String drugParent;

    private BigDecimal doseTotal;
    private String doseUnitOther;
    private String frequencyOther;
    private String route;
    private String therapyReason;
    private String therapyReasonOther;
    private String prophylaxisSpecOther;
    private Integer aeNumber;

    private String infBodySys;
    private String infBodySysOther;
    private String reasonStop;
    private String reasonStopOther;
    private String activeIngredient1;
    private String activeIngredient2;

    private transient Object[] aeNum;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (ConcomitantMedSchedule) super.clone();
    }
}

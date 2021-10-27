package com.acuity.visualisations.model.output.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VitalThin extends TimestampedEntity {
    protected String testGuid;

    protected String subject;
    protected String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    protected LocalDateTime date;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    protected LocalDateTime lpdat;

    protected BigDecimal lpdos;

    protected String testName;
    protected BigDecimal testResult;
    protected String resultUnit;
    protected String anatomicalLocation;
    protected String anatomicalSideOfInterest;
    protected String physicalPosition;
    protected String clinicallySignificant;

    private String protocolScheduleTimepoint;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime lastDoseDate;
    private String lastDoseAmount;
}

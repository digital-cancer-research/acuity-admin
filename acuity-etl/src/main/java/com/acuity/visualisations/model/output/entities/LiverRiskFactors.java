
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
public class LiverRiskFactors extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer potentialHysLawCaseNumber;
    private String liverRiskFactor;
    private String occurrence;
    private String referencePeriod;
    private String details;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime stopDate;
    private String comment;
}


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
public class PregnancyTest extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer visitNumber;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime pTVisitDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime pTSamplingDate;
    private String pTSerum;
    private String pTUrine;
    private String pTPlasma;
    private String pregTest;
}

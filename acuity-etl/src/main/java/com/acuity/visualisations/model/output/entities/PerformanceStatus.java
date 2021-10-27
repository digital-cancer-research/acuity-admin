
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
public class PerformanceStatus extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer visitNumber;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
	private LocalDateTime visitDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
	private LocalDateTime assessmentDate;
    private String performanceStatus;
    private String questionnaire;
}

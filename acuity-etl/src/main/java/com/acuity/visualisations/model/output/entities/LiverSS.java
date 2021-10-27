
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
public class LiverSS extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer potentialHysLawCaseNumber;
    private String value;
    private String occurrence;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime stopDate;
    private String intermittent;
    private String specification;
    private String valueText;
    private String pt;
    private String llt;
    private String hlt;
    private String soc;
}

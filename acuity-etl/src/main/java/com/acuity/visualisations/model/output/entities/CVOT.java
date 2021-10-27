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
public class CVOT extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer aeNumber;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    private String term;
    private String category1;
    private String category2;
    private String category3;
    private String description1;
    private String description2;
    private String description3;
}

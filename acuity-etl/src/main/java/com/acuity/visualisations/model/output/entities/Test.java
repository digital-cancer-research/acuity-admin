package com.acuity.visualisations.model.output.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Test extends TimestampedEntity {

    private String patientGuid;

    private BigDecimal visit;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    private String subject;
    private String part;

    private Integer instanceId;
    private Integer subjectId;
}

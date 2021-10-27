package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CtDna extends TimestampedEntity {

    private static final String YES = "YES";
    private static final String NO = "NO";

    protected String part;
    protected String patientGuid;

    protected String subject;

    private String gene;
    private String mutation;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime sampleDate;
    // Reported Variant Allele Frequency (VAF)
    private Double reportedVAF;

    private String trackedMutation;
    private String visitName;
    private BigDecimal visitNumber;

}

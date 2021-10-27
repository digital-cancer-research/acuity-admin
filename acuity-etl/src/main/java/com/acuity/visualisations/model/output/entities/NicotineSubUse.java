package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NicotineSubUse extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private String category;
    private String type;
    private String otherTypeSpec;
    private String useOccurrence;
    private String currentUseSpec;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime useStartDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime useEndDate;
    private BigDecimal consumption;
    private String useFreqInterval;
    private Integer numberOfPackYears;
    private String typeUseOccurrence;
}

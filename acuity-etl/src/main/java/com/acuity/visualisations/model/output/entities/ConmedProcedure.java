
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
public class ConmedProcedure extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private String value;
    private String pt;
    private String llt;
    private String hlt;
    private String soc;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;
    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;
    private String primaryReason;
    private String primaryReasonOther;
    private String continuesAtStudyDiscCompl;
    private String reason;
    private Integer numberOfLesionsResected;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime dateWoundHealed;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime hospitalDischargeDate;
}

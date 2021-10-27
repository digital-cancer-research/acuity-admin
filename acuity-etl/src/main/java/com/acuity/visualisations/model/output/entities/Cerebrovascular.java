
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
public class Cerebrovascular extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private Integer aeNumber;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
	private LocalDateTime startDate;
    private String term;
    private String eventType;
    private String primaryIschemicStroke;
    private String traumatic;
    private String intracranialHemorrhageLocation;
    private String intracranialHemorrhageLocationOther;
    private String symptomsDuration;
    private String mRSPriorToStroke;
    private String mRSDuringStrokeHospitalisation;
    private String mRSAtVisitOrAfterStroke;
    private String comment;
}

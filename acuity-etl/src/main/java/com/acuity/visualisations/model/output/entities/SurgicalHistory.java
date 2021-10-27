package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author adavliatov.
 * @since 16.12.2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SurgicalHistory extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private String procedure;
    private String current;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    private String llt;
    private String pt;
    private String hlt;
    private String soc;
}

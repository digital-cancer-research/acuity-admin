package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RecistNonTargetLesion extends TimestampedEntity {
    private String patientGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime lesionDate;

    private String lesionSite;
    private String lesionPresent;
    private String response;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private BigDecimal visitNumber;

    @Override
    public boolean isValid() {
        return subject != null && lesionDate != null;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("lesionDate", lesionDate)
                .append("lesionSite", lesionSite)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("lesionDate", lesionDate)
                .append("lesionSite", lesionSite)
                .append("lesionPresent", lesionPresent)
                .append("response", response)
                .append("visitDate", visitDate)
                .append("visitNumber", visitNumber)
                .toString();
    }
}

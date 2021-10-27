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
public class RecistTargetLesion extends TimestampedEntity {
    private String patientGuid;

    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime lesionDate;

    private String lesionSite;
    private String lesionNumber;
    private String lesionPresent;
    private BigDecimal lesionDiameter;
    private String investigatorsResponse;
    private String lesionNoLongerMeasurable;
    private String methodOfAssessment;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private BigDecimal visitNumber;

    public RecistTargetLesion() {
        super();
    }

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
                .append("lesionNumber", lesionNumber)
                .append("lesionNoLongerMeasurable", lesionNoLongerMeasurable)
                .append("methodOfAssessment", methodOfAssessment)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("subject", subject)
                .append("part", part)
                .append("lesionDate", lesionDate)
                .append("lesionSite", lesionSite)
                .append("lesionNumber", lesionNumber)
                .append("lesionPresent", lesionPresent)
                .append("lesionDiameter", lesionDiameter)
                .append("visitDate", visitDate)
                .append("visitNumber", visitNumber)
                .append("investigatorsResponse", investigatorsResponse)
                .append("lesionNoLongerMeasurable", lesionNoLongerMeasurable)
                .append("methodOfAssessment", methodOfAssessment)
                .toString();
    }
}

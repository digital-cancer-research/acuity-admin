package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;


@Getter
@Setter
public class PkConcentration extends TimestampedEntity {
    private String patientGuid;
    private String subject;
    private String part;
    private String spcIdentifier;
    private String analyte;
    private Double analyteConcentration;
    private String analyteConcentrationUnit;
    private BigDecimal lowerLimit;
    private String treatmentCycle;
    private String treatment;
    private String treatmentSchedule;
    private String comment;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("spcIdentifier", spcIdentifier)
                .append("analyte", analyte)
                .append("treatment", treatment)
                .append("treatmentCycle", treatmentCycle)
                .append("treatmentSchedule", treatmentSchedule)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("spcIdentifier", spcIdentifier)
                .append("analyte", analyte)
                .append("treatment", treatment)
                .append("treatmentCycle", treatmentCycle)
                .append("treatmentSchedule", treatmentSchedule)

                .append("analyteConcentration", analyteConcentration)
                .append("analyteConcentrationUnit", analyteConcentrationUnit)
                .append("lowerLimit", lowerLimit)
                .append("comment", comment)
                .toString();
    }

}

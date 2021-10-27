package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
public class AeSeverity extends TimestampedEntity {

    private String aeGuid;

    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime startDate;

    @AcuityField(transform = AcuityFieldTransformation.TERMINATING_EVENT_23_59_59)
    private LocalDateTime endDate;

    private String severity;

    private transient AE ae;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeUniqueFields", ae.uniqueFieldsToString())
                .append("startDate", startDate)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("aeAllFields", ae.allFieldsToString())
                .append("startDate", startDate).append("severity", severity)
                .append("endDate", endDate)
                .toString();
    }

    public String getAeUniqueFields() {
        return ae.uniqueFieldsToString();
    }

    public String getAeGuid() {
        return aeGuid;
    }

    public AE getAe() {
        return ae;
    }

    public void setAe(AE ae) {
        this.ae = ae;
    }

    public void setAeGuid(String aeGuid) {
        this.aeGuid = aeGuid;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }


    // These getters below required by EntityDescription.xml
    public String getAePT() {
        return ae.getPT();
    }

    public String getAeHLT() {
        return ae.getHLT();
    }

    public String getAeSOC() {
        return ae.getSOC();
    }

    public String getAeSubject() {
        return ae.getSubject();
    }

    public LocalDateTime getAeStartDate() {
        return ae.getStartDate();
    }

}

package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubjectCharacteristic extends TimestampedEntity {
    private String patientGuid;
    private String subject;
    private String part;

    private BigDecimal visitNumber;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;

    private String ethpop;
    private String sEthpop;

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("ethpop", ethpop)
                .append("sEthpop", sEthpop)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("patientGuid", patientGuid)
                .append("visitNumber", visitNumber)
                .append("visitDate", visitDate)
                .append("ethpop", ethpop)
                .append("sEthpop", sEthpop)
                .toString();
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public BigDecimal getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(BigDecimal visitNumber) {
        this.visitNumber = visitNumber;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public String getEthpop() {
        return ethpop;
    }

    public void setEthpop(String ethpop) {
        this.ethpop = ethpop;
    }

    public String getSEthpop() {
        return sEthpop;
    }

    public void setSEthpop(String sEthpop) {
        this.sEthpop = sEthpop;
    }
}

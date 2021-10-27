package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Country extends TimestampedEntity {

    private String patientGuid;

    private String countryName;

    private String subject;
    private String part;

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
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

    public Country() {
        initId();
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("study", getStudyName()).
                append("subject", subject).
                append("part", part).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("study", getStudyName()).
                append("subject", subject).
                append("part", part).
                append("countryName", countryName).
                toString();
    }

}

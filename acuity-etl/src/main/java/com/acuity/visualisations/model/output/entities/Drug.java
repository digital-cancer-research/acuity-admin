package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Drug extends TimestampedEntity {

    private String studyGuid;

    private String drugName;

    public Drug() {

    }

    public Drug(String drugName) {
        this.drugName = drugName;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle())
                .append("drugName", drugName)
                .toString();
    }

    @Override
    public String allFieldsToString() {
        return uniqueFieldsToString();
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }
}

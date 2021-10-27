package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LaboratoryGroup extends TimestampedEntity {

    private String studyGuid;
    private String groupName;
    private String labCode;
    private String description;

    public LaboratoryGroup() {
        initId();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("labCode", labCode).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("labCode", labCode).
                append("groupName", groupName).
                append("description", description).
                toString();
    }

}

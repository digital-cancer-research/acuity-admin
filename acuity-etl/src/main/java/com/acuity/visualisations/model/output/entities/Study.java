package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Study extends TimestampedEntity {

    private String projectGuid;
    private String studyGroupGuid;
    private String studyName;
    private String studyDisplay;

    public Study() {
        initId();
    }

    public String getProjectGuid() {
        return projectGuid;
    }

    public void setProjectGuid(String projectGuid) {
        this.projectGuid = projectGuid;
    }

    public String getStudyGroupGuid() {
        return studyGroupGuid;
    }

    public void setStudyGroupGuid(String studyGroupGuid) {
        this.studyGroupGuid = studyGroupGuid;
    }

    @Override
    public String getStudyName() {
        return studyName;
    }

    @Override
    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyDisplay() {
        return studyDisplay;
    }

    public void setStudyDisplay(String studyDisplay) {
        this.studyDisplay = studyDisplay;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("projectName", getProjectName()).
                append("studyName", studyName).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("projectName", getProjectName()).
                append("studyName", studyName).
                append("studyDisplay", studyDisplay).
                toString();
    }

}

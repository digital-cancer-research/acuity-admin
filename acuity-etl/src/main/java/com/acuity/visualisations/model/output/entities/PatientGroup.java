package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PatientGroup extends TimestampedEntity {

    private String studyGuid;
    private String subject;
    private String part;
    private String groupName;
    private String groupingName;

    public PatientGroup() {
        initId();
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("subject", subject).append("part", part)
                .append("groupName", groupName).toString();
    }

    public String getGroupingName() {
        return groupingName;
    }

    public void setGroupingName(String groupingName) {
        this.groupingName = groupingName;
    }
}

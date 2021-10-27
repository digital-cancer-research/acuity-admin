package com.acuity.visualisations.mapping.entity;

import com.acuity.visualisations.mapping.AeSeverityType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProjectRule extends MappingEntity implements DynamicEntity {

    private String drugId;
    private String drugProgrammeName;
    private Boolean acuityEnabled;
    private boolean createDashboard;
    private int totalStudyCount;
    private int numberOfAcuityEnabledStudies;
    private int completed;
    private String createdBy;
    private Date creationDate;

    private List<StudyRule> studyRules;

    private AeSeverityType aeSeverityType;

    public boolean isCreateDashboard() {
        return createDashboard;
    }

    public void setCreateDashboard(boolean createDashboard) {
        this.createDashboard = createDashboard;
    }

}

package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.ProjectRule;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by knml167 on 18/09/2014.
 */
@Getter
@Setter
public class ProjectInfoDTO {

    private ProjectRule projectRule;
    private Boolean canEditProject;
    private Boolean canDeleteProject;
    private Boolean canAddStudy;
    private Boolean canAddInstance;
    private Boolean canAddSummaryTable;


    public ProjectInfoDTO(ProjectRule projectRule) {
        this.projectRule = projectRule;
    }

    public ProjectRule getProjectRule() {
        return projectRule;
    }

    public void setProjectRule(ProjectRule projectRule) {
        this.projectRule = projectRule;
    }

}

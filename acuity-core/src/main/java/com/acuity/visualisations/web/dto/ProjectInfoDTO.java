/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

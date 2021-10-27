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

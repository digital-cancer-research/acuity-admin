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

import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.util.JSONDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyInfoDTO {
    @Getter
    @Setter
    private String studyCode;
    @Getter
    @Setter
    private String studyName;
    @Getter
    @Setter
    private String clinicalStudyName;
    @Getter
    @Setter
    private String clinicalStudyId;
    @Getter
    @Setter
    private String phase;

    @Getter
    @Setter
    private Boolean canEditStudy;
    @Getter
    @Setter
    private Boolean canDeleteStudy;

    @Getter
    @Setter
    @JsonSerialize(using = JSONDateSerializer.class)
    private Date firstSubjectInPlanned;
    @Getter
    @Setter
    @JsonSerialize(using = JSONDateSerializer.class)
    private Date databaseLockPlanned;

    @Getter
    @Setter
    private boolean blinding;
    @Getter
    @Setter
    private boolean randomisation;
    @Getter
    @Setter
    private boolean regulatory;
    @Getter
    @Setter
    private List<FileRuleDTO> fileRules;

    public StudyInfoDTO(StudyRule rule) {
        studyCode = rule.getStudyCode();
        studyName = rule.getStudyName();
        clinicalStudyId = rule.getClinicalStudyId();
        clinicalStudyName = rule.getClinicalStudyName();
        phase = rule.getPhase();
        firstSubjectInPlanned = rule.getFirstSubjectInPlanned();
        databaseLockPlanned = rule.getDatabaseLockPlanned();

        blinding = rule.isBlinding();
        randomisation = rule.isRandomisation();
        regulatory = rule.isRegulatory();
    }

    public void setFileRules(List<FileRule> fileRules) {
        this.fileRules = new ArrayList<>(fileRules.size());
        for (FileRule fileRule : fileRules) {
            this.fileRules.add(new FileRuleDTO(fileRule));
        }
    }
}

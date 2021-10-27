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

package com.acuity.visualisations.web.workflow;

import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.dto.DisclaimerWarningsHolder;
import com.acuity.visualisations.web.dto.MappingStatusDTO;
import com.acuity.visualisations.web.util.Consts;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClinicalStudyWorkflow {
    private List<StudyRule> studySearchResult;
    private StudyRule selectedStudy;
    private ProjectRule parentProject;
    private Map<Long, GroupRuleBase> groupings = new HashMap<>();
    private List<MappingStatusDTO> completeMappings;
    private List<GroupRuleBase> availableProjectGroupings = new ArrayList<>();

    public List<GroupRuleBase> getAvailableProjectGroupings() {
        return availableProjectGroupings;
    }
    public List<StudyRule> getStudySearchResult() {
        return studySearchResult;
    }

    public void setStudySearchResult(List<StudyRule> result) {
        this.studySearchResult = result;
    }

    public void setSelectedStudy(StudyRule study) {
        selectedStudy = study;
    }

    public StudyRule getSelectedStudy() {
        return selectedStudy;
    }

    public ProjectRule getParentProject() {
        return parentProject;
    }

    public void setParentProject(ProjectRule parentProject) {
        this.parentProject = parentProject;
    }

    public List<GroupRuleBase> getGroupings() {
        return new ArrayList<>(groupings.values());
    }

    public void setGroupings(List<GroupRuleBase> groups) {
        this.groupings = new HashMap<>();
        for (GroupRuleBase group : groups) {
            this.groupings.put(group.getId(), group);
        }
    }

    public void addGrouping(GroupRuleBase group) {
        groupings.put(group.getId(), group);
    }

    public void deleteGroup(Long groupId) {
        groupings.remove(groupId);
    }

    public GroupRuleBase getGroup(Long groupId) {
        return groupings.get(groupId);
    }

    public List<MappingStatusDTO> getCompleteMappings() {
        return completeMappings;
    }

    public void setCompleteMappings(List<MappingStatusDTO> completeMappings) {
        this.completeMappings = completeMappings;
    }

    public String getDisclaimerWarning() {
        Map<String, String> studyDisclaimerMap = DisclaimerWarningsHolder.instance().getStudyDisclaimerMap();
        String key = (selectedStudy.isBlinding() ? "1" : "0")
                .concat(selectedStudy.isRandomisation() ? "1" : "0")
                .concat(selectedStudy.isRegulatory() ? "1" : "0");
        String warning = studyDisclaimerMap.get(key);
        return StringUtils.hasText(warning) ? warning : Consts.EMPTY_STRING;
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrugProgramWorkflow {
    private List<ProjectRule> searchProjectResult;
    private List<StudyRule> searchStudyResult;
    private ProjectRule selectedProject;
    private SortedMap<String, GroupRuleBase> groupings = new TreeMap<String, GroupRuleBase>();

    public void setSearchProjectResult(List<ProjectRule> searchProjectResult) {
        this.searchProjectResult = searchProjectResult;
    }

    public void setSearchStudyResult(List<StudyRule> searchStudyResult) {
        this.searchStudyResult = searchStudyResult;
    }

    public void setSelectedProject(ProjectRule project) {
        selectedProject = project;
    }

    @JsonIgnore
    public List<ProjectRule> getSearchProjectResult() {
        return searchProjectResult;
    }

    public ProjectRule getSelectedProject() {
        return selectedProject;
    }

    @JsonIgnore
    public List<StudyRule> getSearchStudyResult() {
        return searchStudyResult;
    }

    public List<GroupRuleBase> getGroupings() {
        return new ArrayList<GroupRuleBase>(groupings.values());
    }

    public void setGroupings(List<GroupRuleBase> groups) {
        groupings = new TreeMap<String, GroupRuleBase>();
        for (GroupRuleBase group : groups) {
            groupings.put(group.getId().toString() + group.getType(), group);
        }
    }

    public void addGrouping(GroupRuleBase item) {
        if (groupings == null) {
            groupings = new TreeMap<String, GroupRuleBase>();
        }
        groupings.put(item.getId().toString() + item.getType(), item);
    }

    public void editGrouping(GroupRuleBase item) {
        GroupRuleBase oldItem = groupings.get(item.getId().toString() + item.getType());
        if (oldItem != null) {
            groupings.put(item.getId().toString() + item.getType(), item);
        }
    }

    public GroupRuleBase getGroup(String id) {
        return groupings.get(id);
    }

    public void deleteGroup(String id) {
        GroupRuleBase oldItem = groupings.get(id);
        if (oldItem != null) {
            groupings.remove(id);
        }
    }

}

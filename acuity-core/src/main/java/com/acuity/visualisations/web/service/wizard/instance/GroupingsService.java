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

package com.acuity.visualisations.web.service.wizard.instance;

import com.acuity.visualisations.mapping.dao.IAEGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ILabGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ISubjectGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ISubjectGroupingTypeDao;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupRuleBase.ProjectGroupType;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.SubjectGroupRule;
import com.acuity.visualisations.mapping.entity.SubjectGroupingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
public class GroupingsService {

    @Autowired
    private IAEGroupRuleDao aeGroupRuleDao;

    @Autowired
    private ILabGroupRuleDao labGroupRuleDao;

    @Autowired
    private ISubjectGroupRuleDao subjectGroupRuleDao;

    @Autowired
    private ISubjectGroupingTypeDao subjectGroupingTypeDao;

    public Map<ProjectGroupType, List<GroupRuleBase>> getProjectGroupings(ProjectRule project) {
        Map<ProjectGroupType, List<GroupRuleBase>> map = new HashMap<ProjectGroupType, List<GroupRuleBase>>();
        map.put(ProjectGroupType.ae, new ArrayList<GroupRuleBase>());
        map.put(ProjectGroupType.lab, new ArrayList<GroupRuleBase>());
        List<GroupRuleBase> groupRules = new ArrayList<GroupRuleBase>();
        groupRules.addAll(aeGroupRuleDao.getByProjectId(project.getId()));
        groupRules.addAll(labGroupRuleDao.getByProjectId(project.getId()));
        for (GroupRuleBase group : groupRules) {
            map.get(group.getType()).add(group);
        }
        return map;
    }

    public List<SubjectGroupRule> getStudyGroupings(StudyRule study) {
        return subjectGroupRuleDao.getByStudyId(study.getId());
    }

    public List<SubjectGroupingType> listSubjectGroupingTypes() {
        return subjectGroupingTypeDao.selectAll();
    }
}

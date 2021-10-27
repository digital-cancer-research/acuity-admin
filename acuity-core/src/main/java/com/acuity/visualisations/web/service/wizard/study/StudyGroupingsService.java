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

package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.dao.IRelationDao;
import com.acuity.visualisations.mapping.dao.ISubjectGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ISubjectGroupValueDao;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupValueBase;
import com.acuity.visualisations.mapping.entity.StudySubjectGrouping;
import com.acuity.visualisations.mapping.entity.SubjectGroupValueRule;
import com.acuity.visualisations.web.util.Consts;
import com.acuity.visualisations.web.util.GroupingsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class StudyGroupingsService {

    @Autowired
    private ISubjectGroupRuleDao subjectGroupRuleDao;

    @Autowired
    private ISubjectGroupValueDao subjectGroupValueDao;

    @Autowired
    private IRelationDao relationDao;

    @Autowired
    private DataProvider provider;


    public List<GroupRuleBase> getStudyGroups(Long id) {
        List<GroupRuleBase> groups = new ArrayList<GroupRuleBase>();
        groups.addAll(subjectGroupRuleDao.getByStudyId(id));
        for (GroupRuleBase group : groups) {
            group.getValues().addAll(subjectGroupValueDao.getByGroupId(group.getId()));
        }
        return groups;
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveGroup(GroupRuleBase group) {
        if (group.getId() == null || group.getId() == 0) {
            subjectGroupRuleDao.insert(group);
        } else {
            subjectGroupRuleDao.update(group);
            //if a group gets updated, we need to remove all associated values before we insert new ones
            subjectGroupValueDao.deleteAll(group);
        }
        for (GroupValueBase val : group.getValues()) {
            val.setGroupId(group.getId());
            subjectGroupValueDao.insert((SubjectGroupValueRule) val);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteGroup(GroupRuleBase group) {
        subjectGroupRuleDao.delete(group);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteGroupValues(GroupRuleBase group) {
        subjectGroupValueDao.deleteAll(group);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveGroupValues(GroupRuleBase grp) {
        for (GroupValueBase val : grp.getValues()) {
            subjectGroupValueDao.insert((SubjectGroupValueRule) val);
        }
    }
    @Transactional(rollbackFor = Throwable.class)
    public String updateGroup(GroupRuleBase group, String sourceType, String remoteLocation, MultipartFile upload) throws Exception {
        if (sourceType.equals("source-local") && upload != null) {
            String message = GroupingsUtils.readGroupingValues(group, upload.getInputStream());
            if (StringUtils.hasText(message)) {
                return message;
            }
        } else {
            if (sourceType.equals("source-remote")) {
                String message = GroupingsUtils.readGroupingValuesFromSharedLocation(group, remoteLocation, provider);
                if (StringUtils.hasText(message)) {
                    return message;
                }
                group.setDataSource(remoteLocation);
            }
        }

        saveGroup(group);
        return Consts.EMPTY_STRING;
    }

    public List<String> getSubjectGroupingNamesFromResultTable(long studyRuleId) {
        return subjectGroupRuleDao.getGroupingNamesFromSubjectTable(studyRuleId);
    }


    public StudySubjectGrouping getSelectedStudySubjectGroupings(long studyId) {
        return relationDao.getSelectedStudySubjectGroupings(studyId);
    }

    public StudySubjectGrouping getAllStudySubjectGroupings(long studyId) {
        return relationDao.getAllStudySubjectGroupings(studyId);
    }
    @Transactional(rollbackFor = Throwable.class)
    public void saveSelectedStudySubjectGroupings(StudySubjectGrouping grouping) {
        relationDao.saveSelectedStudySubjectGroupings(grouping);
    }
}

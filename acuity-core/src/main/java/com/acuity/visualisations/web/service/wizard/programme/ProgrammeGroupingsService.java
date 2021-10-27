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

package com.acuity.visualisations.web.service.wizard.programme;

import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.dao.IAEGroupValueRuleDao;
import com.acuity.visualisations.mapping.dao.IAEGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ILabGroupValueRuleDao;
import com.acuity.visualisations.mapping.dao.ILabGroupRuleDao;
import com.acuity.visualisations.mapping.entity.AEGroupValueRule;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupRuleBase.ProjectGroupType;
import com.acuity.visualisations.mapping.entity.GroupValueBase;
import com.acuity.visualisations.mapping.entity.LabGroupValueRule;
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
public class ProgrammeGroupingsService {

    @Autowired
    private IAEGroupRuleDao aeStudyGroupRuleDao;

    @Autowired
    private IAEGroupValueRuleDao aeGroupValueRuleDao;

    @Autowired
    private ILabGroupRuleDao labStudyGroupRuleDao;

    @Autowired
    private ILabGroupValueRuleDao labGroupValueRuleDao;

    @Autowired
    private DataProvider provider;

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

    @Transactional(rollbackFor = Throwable.class)
    public void saveGroup(GroupRuleBase group) {
        if (group.getId() == null) {
            (group.getType() == ProjectGroupType.ae ? aeStudyGroupRuleDao : labStudyGroupRuleDao).insert(group);
        } else {
            //the group is present in the database, so it gets updated
            (group.getType() == ProjectGroupType.ae ? aeStudyGroupRuleDao : labStudyGroupRuleDao).update(group);

            //makes sense to delete all previous associated groupings in case of data source change
            if (group.getType() == ProjectGroupType.ae) {
                aeGroupValueRuleDao.deleteAll(group);
            } else {
                labGroupValueRuleDao.deleteAll(group);
            }
        }

        for (GroupValueBase val : group.getValues()) {
            val.setGroupId(group.getId());
            if (group.getType() == ProjectGroupType.ae) {
                aeGroupValueRuleDao.insert((AEGroupValueRule) val);
            } else {
                labGroupValueRuleDao.insert((LabGroupValueRule) val);
            }
        }
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void deleteGroupValues(GroupRuleBase group) {
        if (group.getType() == ProjectGroupType.ae) {
            aeGroupValueRuleDao.deleteAll(group);
        } else {
            labGroupValueRuleDao.deleteAll(group);
        }
        for (GroupValueBase val : group.getValues()) {
            if (group.getType() == ProjectGroupType.ae) {
                aeGroupValueRuleDao.insert((AEGroupValueRule) val);
            } else {
                labGroupValueRuleDao.insert((LabGroupValueRule) val);
            }
        }
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveGroupValues(GroupRuleBase group) {
        deleteGroupValues(group);
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void deleteGroup(GroupRuleBase group) {
        if (group.getType() == ProjectGroupType.ae) {
            aeGroupValueRuleDao.deleteAll(group);
            aeStudyGroupRuleDao.delete(group);
        } else {
            labGroupValueRuleDao.deleteAll(group);
            labStudyGroupRuleDao.delete(group);
        }
    }

    public List<GroupRuleBase> getProjectGroups(Long id) {
        List<GroupRuleBase> groups = new ArrayList<GroupRuleBase>();
        groups.addAll(aeStudyGroupRuleDao.getByProjectId(id));
        groups.addAll(labStudyGroupRuleDao.getByProjectId(id));
        for (GroupRuleBase group : groups) {
            if (group.getType() == ProjectGroupType.ae) {
                group.getValues().addAll(aeGroupValueRuleDao.getByGroupId(group.getId()));
            } else {
                group.getValues().addAll(labGroupValueRuleDao.getByGroupId(group.getId()));
            }
        }
        return groups;
    }

}

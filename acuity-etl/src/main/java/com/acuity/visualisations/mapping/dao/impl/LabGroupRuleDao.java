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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.mapping.dao.ILabGroupRuleDao;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.LabGroupRule;
import org.springframework.stereotype.Repository;

@Repository
public class LabGroupRuleDao extends ProjectGroupRuleDaoBase implements ILabGroupRuleDao {

    @Override
    protected String getTableName() {
        return "MAP_LAB_GROUP_RULE";
    }

    @Override
    protected String getRelationTableName() {
        return "MAP_STUDY_LAB_GROUP";
    }

    @Override
    protected String getGroupRelationId() {
        return "MSLG_LAB_GROUP_ID";
    }

    @Override
    protected String getRelationId() {
        return "MSLG_MSR_ID";
    }

    @Override
    protected GroupRuleBase createInstance() {
        return new LabGroupRule();
    }

    @Override
    protected String getIdColumnName() {
        return "MLGR_ID";
    }

    @Override
    protected String getPrefix() {
        return "MLGR";
    }

    @Override
    protected String getParentColumnName() {
        return "MLGR_PROJECT_ID";
    }

}

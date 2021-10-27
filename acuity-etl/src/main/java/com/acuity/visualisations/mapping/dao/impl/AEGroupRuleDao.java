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

import com.acuity.visualisations.mapping.dao.IAEGroupRuleDao;
import com.acuity.visualisations.mapping.entity.AEGroupRule;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import org.springframework.stereotype.Repository;

@Repository
public class AEGroupRuleDao extends ProjectGroupRuleDaoBase implements IAEGroupRuleDao {

    @Override
    protected String getTableName() {
        return "MAP_AE_GROUP_RULE";
    }

    @Override
    protected String getRelationTableName() {
        return "MAP_STUDY_AE_GROUP";
    }

    @Override
    protected String getGroupRelationId() {
        return "MSAG_AE_GROUP_ID";
    }

    @Override
    protected String getRelationId() {
        return "MSAG_MSR_ID";
    }

    @Override
    protected GroupRuleBase createInstance() {
        return new AEGroupRule();
    }

    @Override
    protected String getIdColumnName() {
        return "MAGR_ID";
    }

    @Override
    protected String getPrefix() {
        return "MAGR";
    }

    @Override
    protected String getParentColumnName() {
        return "MAGR_PROJECT_ID";
    }
}

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

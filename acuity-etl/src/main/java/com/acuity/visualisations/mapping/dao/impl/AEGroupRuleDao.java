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

package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.FieldRule;

import java.util.List;

public interface IDynamicFieldRuleDao {

    FieldRule insertDynamicField(long mappingRuleId, String name);

    FieldRule getDynamicFieldByMappingRuleId(long mappingRuleId);

    void deleteDynamicField(long mappingRuleId);

    List<FieldRule> selectAll();
}

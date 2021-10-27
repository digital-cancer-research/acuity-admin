package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.MappingRule;

public interface IMappingRuleDao extends IBasicDynamicEntityDao<MappingRule> {

	void delete(MappingRule mappingRule);

	List<MappingRule> getMappingRulesByStudyRule(long studyRuleId);

    List<MappingRule> getMappingRulesForFileRule(long fileRuleId);
}

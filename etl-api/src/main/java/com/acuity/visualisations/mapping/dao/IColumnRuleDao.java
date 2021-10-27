package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.MappingRule;

public interface IColumnRuleDao extends IBasicDynamicEntityDao<ColumnRule> {

	void delete(MappingRule mappingRule);

	List<ColumnRule> getColumnRulesByStudy(long studyId);

    List<ColumnRule> getColumnRulesForFileRule(long fileRuleId);
}

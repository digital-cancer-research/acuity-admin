package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.ExcludingValue;

public interface IExcludingValueDao extends IBasicDynamicEntityDao<ExcludingValue> {

	List<ExcludingValue> getExcludingValuesByStudyRule(long studyRuleId);

	void deleteStudyExcludingValues(long studyRuleId);

	void insertExcludingValues(List<ExcludingValue> excludingValues);

}

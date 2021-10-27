package com.acuity.visualisations.mapping.dao;

import java.util.Collection;

import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.SubjectGroupValueRule;

public interface ISubjectGroupValueDao extends IBasicDynamicEntityDao<SubjectGroupValueRule> {

	void deleteAll(GroupRuleBase group);

	Collection<SubjectGroupValueRule> getByGroupId(Long id);

}

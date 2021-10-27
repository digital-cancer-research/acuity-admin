package com.acuity.visualisations.mapping.dao;

import java.util.Collection;

import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.LabGroupValueRule;

public interface ILabGroupValueRuleDao extends IBasicDynamicEntityDao<LabGroupValueRule> {

	void deleteAll(GroupRuleBase group);

	Collection<LabGroupValueRule> getByGroupId(Long id);

}

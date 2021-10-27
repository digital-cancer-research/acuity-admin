package com.acuity.visualisations.mapping.dao;

import java.util.Collection;

import com.acuity.visualisations.mapping.entity.AEGroupValueRule;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;

public interface IAEGroupValueRuleDao extends IBasicDynamicEntityDao<AEGroupValueRule> {

	void deleteAll(GroupRuleBase group);

	Collection<AEGroupValueRule> getByGroupId(Long id);

}

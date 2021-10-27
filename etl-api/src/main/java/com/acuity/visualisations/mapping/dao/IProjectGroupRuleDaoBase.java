package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.GroupRuleBase;

public interface IProjectGroupRuleDaoBase extends IBasicDynamicEntityDao<GroupRuleBase> {

	List<GroupRuleBase> listGropingsByRelation(Long relatedId);

	void delete(GroupRuleBase group);

	List<GroupRuleBase> getByProjectId(Long id);

}

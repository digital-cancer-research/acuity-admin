package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.SubjectGroupRule;

public interface ISubjectGroupRuleDao extends IProjectGroupRuleDaoBase {

	List<SubjectGroupRule> getByStudyId(Long id);

	List<String> getGroupingNamesFromSubjectTable(long studyRuleId);
}

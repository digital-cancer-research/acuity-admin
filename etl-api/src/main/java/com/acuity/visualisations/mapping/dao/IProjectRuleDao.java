package com.acuity.visualisations.mapping.dao;

import java.util.Collection;
import java.util.List;

import com.acuity.visualisations.mapping.entity.ProjectRule;

public interface IProjectRuleDao extends IBasicDynamicEntityDao<ProjectRule> {

	List<ProjectRule> searchByDrugs(Collection<String> drugIds);

	ProjectRule getProjectByDrug(String drugId);

	boolean isProjectExistByDrug(String drugId);

	void fillSearchQueryWorker(String query, QuerySearchWorker<ProjectRule> worker);

	List<ProjectRule> search(String query);

	List<ProjectRule> getCompletedProjects();

	ProjectRule getProjectById(long projectId);

	void removeProject(Long projectId);

	void setProjectCompleted(Long id);

	Integer listedInACUITYCount(List<String> drugIds);

	List<ProjectRule> getAllProjectRules();
}

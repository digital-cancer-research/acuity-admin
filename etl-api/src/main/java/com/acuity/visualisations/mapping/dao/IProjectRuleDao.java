/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

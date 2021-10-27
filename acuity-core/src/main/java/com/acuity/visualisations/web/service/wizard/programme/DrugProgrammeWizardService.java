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

package com.acuity.visualisations.web.service.wizard.programme;

import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.web.dao.cdbp.CDBPProjectDao;
import com.acuity.visualisations.web.service.AdminService;
import com.acuity.visualisations.web.service.AuditService;
import com.acuity.visualisations.web.service.CleanupTask;
import com.acuity.visualisations.web.service.Hook;
import com.acuity.visualisations.web.service.ShutdownService;
import com.acuity.visualisations.web.util.Consts;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
@Transactional(readOnly = true)
@Service
@Slf4j
public class DrugProgrammeWizardService {

    public static final Integer PROJECT_COMPLETED = 1;
    public static final Integer PROJECT_UNCOMPLETED = 0;

    private ExecutorService executor;
    private Thread cleanUpWorker;
    private Map<String, QuerySearchWorker<?>> queryWorkers;

    @Autowired
    private CDBPProjectDao cdbpProjectDao;

    @Autowired
    private ShutdownService shutdownService;

    @Autowired
    private IProjectRuleDao projectRuleDao;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AdminService adminService;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(Consts.N_THREADS);
        queryWorkers = new Hashtable<>();
        CleanupTask task = new CleanupTask(queryWorkers, Consts.CLEANUP_MILLIS);
        cleanUpWorker = new Thread(task, "Programmes query cleanup worker");
        Hook hook = shutdownService.createHook(cleanUpWorker);
        task.setHook(hook);
        cleanUpWorker.start();
    }

    public String runSearchQuery(String query, List<ProjectRule> acuityResults, List<ProjectRule> cdbpResults) {
        String keyQuery = UUID.randomUUID().toString();
        log.debug("Start search query with id: {}", keyQuery);
        QuerySearchWorker<ProjectRule> queryWorker = new QuerySearchWorker<>(acuityResults, cdbpResults);
        projectRuleDao.fillSearchQueryWorker("%" + query + "%", queryWorker);
        cdbpProjectDao.fillSearchQueryWorker("%" + query + "%", queryWorker);
        queryWorkers.put(keyQuery, queryWorker);
        executor.execute(queryWorker);
        return keyQuery;
    }

    /**
     * Merges drug program search results from ACUITY and CDBP.
     * Actually, ACUITY results should override CDBP result but `totalStudiesCount` field is to be extracted from CDBP.
     *
     * @param acuityProjects ACUITY search results
     * @param cdbpProjects  CDBP search results
     * @return merged SortedMap (ordered by drug) satisfying requirements above
     * @throws SchedulerException
     */
    public SortedMap<String, ProjectRule> getSearchProjectResult(List<ProjectRule> acuityProjects, List<ProjectRule> cdbpProjects) throws SchedulerException {
        return createResultSearch(
                ofNullable(acuityProjects)
                        .orElse(emptyList())
                        .stream()
                        .collect(toMap(ProjectRule::getDrugId, Function.identity())),
                ofNullable(cdbpProjects)
                        .orElse(emptyList())
                        .stream()
                        .collect(toMap(ProjectRule::getDrugId, Function.identity())));
    }

    private SortedMap<String, ProjectRule> createResultSearch(Map<String, ProjectRule> acuityProjects, Map<String, ProjectRule> cdbpProjects)
            throws SchedulerException {
        final Set<String> cdbpProjectIds = cdbpProjects
                .keySet()
                .stream()
                .filter(cdbpProject -> !acuityProjects.containsKey(cdbpProject))
                .collect(Collectors.toSet());

        //find CDBP projects in ACUITY
        projectRuleDao
                .searchByDrugs(cdbpProjectIds)
                .forEach(projectRule -> acuityProjects.putIfAbsent(projectRule.getDrugId(), projectRule));

        //add CDBP projects not presented in ACUITY & update totalStudyInfo
        cdbpProjects
                .values()
                .forEach(cdbpPrj -> {
                            acuityProjects.compute(cdbpPrj.getDrugId(),
                                    (prjId, acuityPrj) -> {
                                        if (acuityPrj == null) {
                                            return cdbpPrj;
                                        } else {
                                            acuityPrj.setTotalStudyCount(cdbpPrj.getTotalStudyCount());
                                            return acuityPrj;
                                        }
                                    });
                        }
                );
        return new TreeMap<>(acuityProjects);
    }

    /**
     * Perform search over ACUITY (by drug name) and CDBP (by drug name & by drug program study names).
     * Merge results according to {@link #getSearchProjectResult(List, List)} getSearchProjectResult} method.
     *
     * @param query user search query
     * @return merged SortedMap (ordered by drug) satisfying requirements above
     * @throws SchedulerException
     */
    public SortedMap<String, ProjectRule> searchProject(String query) throws SchedulerException {
        Map<String, ProjectRule> acuityProjects = projectRuleDao
                .search("%" + query + "%")
                .stream()
                .collect(toMap(ProjectRule::getDrugId, Function.identity()));
        Map<String, ProjectRule> cdbpProjects = cdbpProjectDao
                .searchProjects("%" + query + "%")
                .stream()
                .collect(toMap(ProjectRule::getDrugId, Function.identity()));

        return createResultSearch(acuityProjects, cdbpProjects);
    }

    /**
     * @return number of different available drug programmes in CDBP
     */
    public int getTotalDrugProgrammesCount() {
        List<String> cdbpDrugProgrammesIds = cdbpProjectDao.getCDBPDrugIds();
        Integer acuityTotalCount = projectRuleDao.listedInACUITYCount(cdbpDrugProgrammesIds);
        int result = cdbpDrugProgrammesIds.size() + acuityTotalCount;
        return result;
    }

    /**
     * Saves or updates a Drug Programme in the database
     *
     * @param project Drug Programme to update
     */
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    @PreAuthorize("@permissionHelper.isGlobalAdmin(authentication)")
    public Long saveProject(ProjectRule project) {
        Long id;
        if (project.getId() == null) {
            id = projectRuleDao.insert(project);
        } else {
            projectRuleDao.update(project);
            id = project.getId();
        }
        return id;
    }


    /**
     * @param id Drug Programme id
     * @return DrugProgramme using the {@code id} specified
     */
    public ProjectRule getProjectById(Long id) {
        return projectRuleDao.getProjectById(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    @PreAuthorize("@permissionHelper.isCurrentUserDrugProgrammeAdmin(#id)")
    public void projectCompleted(Long id) {
        projectRuleDao.setProjectCompleted(id);
    }

    public ProjectRule getProjectByDrug(String drugId) {
        return projectRuleDao.getProjectByDrug(drugId);
    }

    public ProjectRule getProjectInCDBPByCode(String drugId) {
        List<ProjectRule> projectRules = cdbpProjectDao.searchProjects(drugId);
        if (!CollectionUtils.isEmpty(projectRules)) {
            return projectRules.get(0);
        }
        return null;
    }

    public boolean isProjectExistByDrug(String drugId) {
        return projectRuleDao.isProjectExistByDrug(drugId);
    }

    public void cancelQuery(String queryId) {
        if (!StringUtils.hasText(queryId)) {
            return;
        }
        QuerySearchWorker<?> querySearchWorker = queryWorkers.get(queryId);
        if (querySearchWorker == null) {
            log.info("Could not find query with id: {}", queryId);
            return;
        }
        querySearchWorker.cancelQuery();
    }

    public void getStatus(String queryId, DeferredResult<Boolean> result) {
        if (!StringUtils.hasText(queryId)) {
            return;
        }
        QuerySearchWorker<?> querySearchWorker = queryWorkers.get(queryId);
        if (querySearchWorker == null) {
            log.info("Could not find query with id: {}", queryId);
            return;
        }
        querySearchWorker.getStatus(result);
    }
}

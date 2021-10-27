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

package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.dao.IClinicalStudyDao;
import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudyRule.Status;
import com.acuity.visualisations.service.IEmailService;
import com.acuity.visualisations.web.dao.cdbp.CDBPStudyDao;
import com.acuity.visualisations.web.service.CleanupTask;
import com.acuity.visualisations.web.service.Hook;
import com.acuity.visualisations.web.service.ShutdownService;
import com.acuity.visualisations.web.service.wizard.programme.DrugProgrammeWizardService;
import com.acuity.visualisations.web.util.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class StudyWizardService {


    @Autowired
    private CDBPStudyDao cdbpStudyDao;

    @Autowired
    private IProjectRuleDao projectRuleDao;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Autowired
    private IClinicalStudyDao clinicalStudyDao;

    @Autowired
    private IEmailService emailService;

    private ExecutorService executor;
    private Thread cleanUpWorker;
    private Map<String, QuerySearchWorker<?>> queryWorkers;

    @Autowired
    private ShutdownService shutdownService;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(Consts.N_THREADS);
        queryWorkers = new Hashtable<>();
        CleanupTask task = new CleanupTask(queryWorkers, Consts.CLEANUP_MILLIS);
        cleanUpWorker = new Thread(task, "Studies query cleanup worker");
        Hook hook = shutdownService.createHook(cleanUpWorker);
        task.setHook(hook);
        cleanUpWorker.start();
    }

    private boolean isExistStudyInCDBP(String code, List<StudyRule> cdbpStudies) {
        for (StudyRule cdbpStudy : cdbpStudies) {
            if (cdbpStudy.getStudyCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    public String runSearchQuery(String query, List<StudyRule> acuityResults, List<StudyRule> cdbpResults) {
        String keyQuery = UUID.randomUUID().toString();
        log.debug("Start search query with id: {}", keyQuery);
        QuerySearchWorker<StudyRule> queryWorker = new QuerySearchWorker<>(acuityResults, cdbpResults);
        studyRuleDao.fillSearchQueryWorker(query, queryWorker);
        cdbpStudyDao.fillSearchQueryWorker(query, queryWorker);
        queryWorkers.put(keyQuery, queryWorker);
        executor.execute(queryWorker);
        return keyQuery;
    }


    public SortedMap<String, StudyRule> getSearchStudyResult(List<StudyRule> acuityStudies, List<StudyRule> cdbpStudies) {
        List<StudyRule> studies = new ArrayList<>();
        studies.addAll(cdbpStudies);
        for (StudyRule study : acuityStudies) {
            if (!isExistStudyInCDBP(study.getStudyCode(), cdbpStudies)) {
                studies.add(study);
            }
        }

        if (CollectionUtils.isEmpty(studies)) {
            return new TreeMap<>();
        }

        List<String> drugProgrammes = new ArrayList<>();
        for (StudyRule study : studies) {
            drugProgrammes.add(study.getDrugProgramme());
        }

        List<ProjectRule> projects = projectRuleDao.searchByDrugs(drugProgrammes);

        List<StudyRule> result = new ArrayList<>();

        for (StudyRule study : studies) {
            StudyRule studyToAdd = study;
            for (ProjectRule project : projects) {
                if (project.getDrugId().equals(study.getDrugProgramme()) /*|| project.getId().equals(study.getProjectId())*/) {
                    studyToAdd = studyRuleDao.getByCode(study.getStudyCode());
                    if (studyToAdd == null) {
                        studyToAdd = study;
                        studyToAdd.setEnabled(true);
                        studyToAdd.setProjectId(project.getId());
                    }
                    if (project.getCompleted() == DrugProgrammeWizardService.PROJECT_UNCOMPLETED) {
                        studyToAdd.setStatus(Status.notInAcuity);
                    } else {
                        if (project.getCompleted() == DrugProgrammeWizardService.PROJECT_COMPLETED
                                && studyToAdd.getStatus() == Status.notInAcuity) {
                            studyToAdd.setStatus(Status.readyToMap);
                        }
                    }
                    break;
                }
            }
            result.add(studyToAdd);
        }
        SortedMap<String, StudyRule> sortedResult = new TreeMap<String, StudyRule>();
        for (StudyRule studyRule : result) {
            sortedResult.put(studyRule.getStudyCode(), studyRule);
        }

        return sortedResult;
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    @PreAuthorize("@permissionHelper.isCurrentUserDrugProgrammeAdmin(#study.getProjectId())")
    public Long saveStudy(StudyRule study) {
        Long id;
        clinicalStudyDao.insertOrUpdateClinicalStudy(study.getProjectId(), study.getClinicalStudyId(), study.getClinicalStudyName());
        if (study.getId() == null) {
            study.setStatus(Status.readyToMap);
            id = studyRuleDao.insert(study);
        } else {
            StudyRule oldStudy = getStudyById(study.getId());
            if (!Objects.equals(oldStudy.isBlinding(), study.isBlinding())
                    || !Objects.equals(oldStudy.isRandomisation(), study.isRandomisation())
                    || !Objects.equals(oldStudy.isRegulatory(), study.isRegulatory())
                    ) {
                //send email
                emailService.sendStudyConfigChangedEmail(oldStudy, study);
            }
            studyRuleDao.update(study);
            id = study.getId();
        }
        return id;
    }

    public StudyRule getStudyById(Long studyId) {
        return studyRuleDao.getStudyById(studyId);
    }

    public StudyRule getStudyInACUITYByCode(String studyCode) {
        return studyRuleDao.getStudyByCode(studyCode);
    }

    public StudyRule getStudyInCDBPByCode(String studyCode) {
        List<StudyRule> studyRules = cdbpStudyDao.searchExactStudy(studyCode);
        if (!CollectionUtils.isEmpty(studyRules)) {
            return studyRules.get(0);
        }
        return null;
    }

    public List<ProjectRule> searchProject(String query) {
        return projectRuleDao.search("%" + query + "%");
    }

    public List<ProjectRule> getCompletedProjects() {
        return projectRuleDao.getCompletedProjects();
    }

    public SortedMap<String, StudyRule> getStudies(List<Long> ids) {
        List<StudyRule> result = studyRuleDao.getStudies(ids);
        SortedMap<String, StudyRule> sortedResult = new TreeMap<String, StudyRule>();
        for (StudyRule studyRule : result) {
            sortedResult.put(studyRule.getStudyCode(), studyRule);
        }

        return sortedResult;
    }

    public ProjectRule getParentProject(StudyRule study) {
        if (study.getProjectId() == null) {
            List<ProjectRule> projectRules = projectRuleDao.searchByDrugs(Collections.singleton(study.getDrugProgramme()));
            if (CollectionUtils.isEmpty(projectRules)) {
                return null;
            }
            return projectRules.get(0);
        }
        return projectRuleDao.getProjectById(study.getProjectId());

    }

    public int getTotalStudiesCount() {
        Set<String> acuityStudyCodes = new HashSet<>(studyRuleDao.getStudyCodes());
        List<String> cdbpStudyCodes = cdbpStudyDao.getStudyCodes();

        acuityStudyCodes.addAll(cdbpStudyCodes);
        return acuityStudyCodes.size();
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
}

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

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.dal.dao.IStudyDaoExternal;
import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UIFunctionsManager {

    @Autowired
    private IStudyDaoExternal studyDao;

    @Autowired
    private ExtendedJobExecutionDao customJobExecutionDao;

    @Transactional
    public void deleteStudies(Map<String, List<String>> studies) {
        studies.entrySet().stream().flatMap(e -> e.getValue().stream()).forEach(std -> {
            studyDao.delete(std);
        });
    }

    @Transactional
    public JobExecution getLatestJobExecution(String projectName, String studyCode, String jobExecutionGuid, String jobName) {
        JobExecution latestJobExecution = null;
        if (jobExecutionGuid != null) {
            latestJobExecution = getLatestJobExecution(new BatchJobExecutionKey(projectName, studyCode, jobExecutionGuid), jobName);
        }
        return latestJobExecution == null ? customJobExecutionDao.getLatestJobExecution(projectName, studyCode, jobName) : latestJobExecution;
    }

    @Transactional
    public JobExecution getLatestJobExecution(String projectName, String studyCode, String jobName) {
        return getLatestJobExecution(projectName, studyCode, null, jobName);
    }

    @Transactional
    public Map<String, Map<String, JobExecution>> getLatestJobExecution(final Map<String, List<String>> keys, String jobName) {
        return customJobExecutionDao.getLatestJobExecution(keys, jobName);
    }

    @Transactional
    public boolean isStudyInDatabase(String projectName, String studyCode) {
        Map<String, List<String>> studies = new HashMap<String, List<String>>();
        List<String> studiesList = new ArrayList<String>();
        studiesList.add(studyCode);
        studies.put(projectName, studiesList);
        Map<String, Map<String, Integer>> studyCount = studyDao.getStudyCount(studies);
        return studyCount.containsKey(projectName) && studyCount.get(projectName).containsKey(studyCode)
                && studyCount.get(projectName).get(studyCode) > 0;
    }

    @Transactional
    public Map<String, Map<String, Boolean>> isStudyInDatabase(Map<String, List<String>> studies) {
        Map<String, Map<String, Integer>> studyCount = studyDao.getStudyCount(studies);
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        for (Map.Entry<String, List<String>> datasetsByStudy : studies.entrySet()) {
            String projectName = datasetsByStudy.getKey();
            result.putIfAbsent(projectName, new HashMap<>());
            Map<String, Boolean> resultByProject = result.get(projectName);
            for (String studyCode : datasetsByStudy.getValue()) {
                resultByProject.put(studyCode, studyCount.containsKey(projectName) && studyCount.get(projectName).containsKey(studyCode)
                        && studyCount.get(projectName).get(studyCode) > 0);
            }
        }

        return result;
    }

    @Transactional
    public Map<BatchJobExecutionKey, JobExecution> getLatestJobExecution(List<BatchJobExecutionKey> keys, String jobName) {
        return customJobExecutionDao.getLatestJobExecutionByFullKeyAndJobName(keys, jobName);
    }

    @Transactional
    public JobExecution getLatestJobExecution(BatchJobExecutionKey key, String jobName) {
        List<BatchJobExecutionKey> keys = new ArrayList<BatchJobExecutionKey>();
        keys.add(key);
        Map<BatchJobExecutionKey, JobExecution> latestJobExecutionByFullKey = customJobExecutionDao.getLatestJobExecutionByFullKeyAndJobName(keys, jobName);
        return latestJobExecutionByFullKey.get(key);
    }

    @Transactional
    public Map<String, Map<String, Boolean>> isStudyScheduledClean(Map<String, List<String>> studies) {
        return studyDao.getStudyScheduledCleanFlag(studies);
    }

    @Transactional
    public Map<String, Map<String, Boolean>> isAmlEnabledForStudy() {
        return studyDao.getStudyAmlEnabledFlag();
    }

    @Transactional
    public boolean isStudyScheduledClean(String projectName, String studyCode) {
        Map<String, List<String>> studies = new HashMap<>();
        List<String> studiesList = new ArrayList<>();
        studiesList.add(studyCode);
        studies.put(projectName, studiesList);
        Map<String, Map<String, Boolean>> scheduleCleanFlagMap = studyDao.getStudyScheduledCleanFlag(studies);
        return scheduleCleanFlagMap.containsKey(projectName) && scheduleCleanFlagMap.get(projectName).containsKey(studyCode)
                && scheduleCleanFlagMap.get(projectName).get(studyCode);
    }

    @Transactional
    public boolean isStudyEtlStatusReset(String studyCode) {
        return studyDao.resetStudyEtlStatus(studyCode) > 0;
    }
}

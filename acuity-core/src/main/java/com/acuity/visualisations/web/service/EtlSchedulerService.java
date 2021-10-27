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

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.dal.dao.IStudyDaoExternal;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.util.JsonUtil;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.acuity.visualisations.util.JobLauncherConsts.AML_GROUP_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.AML_JOB_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.ETL_GROUP_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.ETL_JOB_NAME;

@Component
public class EtlSchedulerService {
    private static final String PROJECT_NAME = "projectName";

    @Data
    public static class JobInfo {
        private String studyCode;
        private String projectName;
        private JobKey jobKey;
        private JobExecutionEvent.ExecutionState executionState;
    }

    @Autowired
    private UIFunctionsManager functionsManager;

    @Autowired
    private IStudyDaoExternal studyDao;

    @Autowired
    private SchedulerStatusStorage schedulerStatusStorage;

    private static final Logger LOGGER = LoggerFactory.getLogger(EtlSchedulerService.class);

    @Autowired
    @Qualifier("schedulerFactory")
    private Scheduler scheduler;


    @LogMeAround
    public JSONObject getJobs() throws Exception {
        Map<String, Map<String, JobInfo>> jobInfos = getSchedulerInfo(ETL_GROUP_NAME);
        Map<String, Map<String, JobInfo>> amlJobInfos = getSchedulerInfo(AML_GROUP_NAME);
        Map<String, Map<String, JobInfo>> queuedJobInfos = getQueuedJobs();
        Map<String, List<String>> jobRepositoryParams = getJobRepositoryParamsFromInfo(jobInfos);
        Map<String, List<String>> amlJobRepositoryParams = getJobRepositoryParamsFromInfo(amlJobInfos);
        final JSONObject resultJSON = new JSONObject();
        final List<JSONObject> resultList = new ArrayList<>();

        Map<String, Map<String, JobExecution>> jobExecutions = functionsManager.getLatestJobExecution(jobRepositoryParams, ETL_JOB_NAME);
        Map<String, Map<String, Boolean>> studyInDatabase = functionsManager.isStudyInDatabase(jobRepositoryParams);
        Map<String, Map<String, Boolean>> studyScheduledClean = functionsManager.isStudyScheduledClean(jobRepositoryParams);
        Map<String, Map<String, JobExecution>> amlJobExecutions = functionsManager.getLatestJobExecution(amlJobRepositoryParams, AML_JOB_NAME);
        Map<String, Map<String, Boolean>> amlEnabledByProjectByStudy = functionsManager.isAmlEnabledForStudy();
        for (Map.Entry<String, Map<String, JobInfo>> entry : jobInfos.entrySet()) {
            for (Map.Entry<String, JobInfo> entry1 : entry.getValue().entrySet()) {
                JobInfo jobInfo = entry1.getValue();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(PROJECT_NAME, jobInfo.projectName);
                jsonObject.put("studyCode", jobInfo.studyCode);
                Boolean amlEnabledForStudy = amlEnabledByProjectByStudy.getOrDefault(jobInfo.projectName, new HashMap<>())
                        .get(jobInfo.studyCode);
                jsonObject.put("amlEnabled", amlEnabledForStudy);
                List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobInfo.jobKey);

                JsonUtil.setTriggerToJson(jsonObject, triggersOfJob.stream().findFirst().orElse(null));

                JobExecution latestJobExecution = jobExecutions.getOrDefault(jobInfo.projectName, new HashMap<>())
                        .get(jobInfo.studyCode);
                JsonUtil.setJobExecutionToJson(jsonObject, latestJobExecution);

                if (queuedJobInfos.containsKey(jobInfo.projectName)
                        && queuedJobInfos.get(jobInfo.projectName).containsKey(jobInfo.studyCode)) {
                    JsonUtil.setSchedulerJobExecutionToJson(jsonObject,
                            queuedJobInfos.get(jobInfo.projectName).get(jobInfo.studyCode).executionState);
                }
                Boolean inDatabase = studyInDatabase.getOrDefault(jobInfo.projectName, new HashMap<>())
                        .getOrDefault(jobInfo.studyCode, false);
                JsonUtil.setStudyInDatabase(jsonObject, inDatabase);

                Boolean scheduledCleanFlag = studyScheduledClean.getOrDefault(jobInfo.projectName, new HashMap<>())
                        .getOrDefault(jobInfo.studyCode, false);
                JsonUtil.setScheduledCleanFlag(jsonObject, scheduledCleanFlag);

                JobExecution lastAmlJobExecution = amlJobExecutions.getOrDefault(jobInfo.projectName, new HashMap<>())
                        .get(jobInfo.studyCode);
                JsonUtil.setAmlJobDataToJson(jsonObject, lastAmlJobExecution, amlEnabledForStudy);

                resultList.add(jsonObject);
            }
        }
        resultList.sort((o1, o2) -> {
            try {
                return ((String) o1.get(PROJECT_NAME)).compareTo((String) o2.get(PROJECT_NAME));
            } catch (JSONException e) {
                return 0;
            }
        });
        final JSONArray resultArray = new JSONArray(resultList);
        resultJSON.put("tasks", resultArray);
        return resultJSON;
    }

    @LogMeAround
    public JSONObject triggerJob(String projectName, String studyCode) throws Exception {

        Optional<JSONObject> jsonObject = getErrorObject(projectName, studyCode);
        if (jsonObject.isPresent()) {
            return jsonObject.get();
        }

        Map<String, Map<String, JobInfo>> jobInfos = getSchedulerInfo(ETL_GROUP_NAME);
        JobInfo jobInfo = jobInfos.get(projectName).get(studyCode);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobInfo.jobKey).startNow().build();
        scheduler.scheduleJob(trigger);
        return new JSONObject();
    }

    @LogMeAround
    public JSONObject triggerAmlJob(String projectName, String studyCode) throws SchedulerException {
        Map<String, Map<String, JobInfo>> jobInfos = getSchedulerInfo(AML_GROUP_NAME);
        JobInfo jobInfo = jobInfos.get(projectName).get(studyCode);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobInfo.jobKey).startNow().build();
        scheduler.scheduleJob(trigger);
        return new JSONObject();
    }

    @LogMeAround
    public JSONObject clean(String projectName, String studyCode) throws Exception {

        Map<String, List<String>> studies = new HashMap<>();
        studies.put(projectName, Collections.singletonList(studyCode));
        JSONObject jsonObject = new JSONObject();
        try {
            functionsManager.deleteStudies(studies);
            JsonUtil.setStudyInDatabase(jsonObject, functionsManager.isStudyInDatabase(projectName, studyCode));
        } catch (Exception e) {
            JsonUtil.setStudyInDatabase(jsonObject, "Clean failed");
            LOGGER.error("Failed to perform clean for project {} study {}", projectName, studyCode, e);
        }

        return jsonObject;
    }

    @LogMeAround
    public JSONObject resetStudyEtlStatus(String studyCode) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (functionsManager.isStudyEtlStatusReset(studyCode)) {
            JsonUtil.setFailedJobExecutionToJson(jsonObject);
        }
        return jsonObject;
    }

    @LogMeAround
    public JSONObject triggerScheduledClean(String projectName, String studyCode) throws Exception {
        studyDao.setScheduleCleanFlag(studyCode);
        JSONObject jsonObject = new JSONObject();
        JsonUtil.setScheduledCleanFlag(jsonObject, functionsManager.isStudyScheduledClean(projectName, studyCode));
        return jsonObject;
    }

    @LogMeAround
    public JSONObject unsheduleJob(String projectName, String studyCode) throws Exception {
        Map<String, Map<String, JobInfo>> jobInfos = getSchedulerInfo(ETL_GROUP_NAME);
        JobInfo jobInfo = jobInfos.get(projectName).get(studyCode);
        List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobInfo.jobKey);
        if (!triggersOfJob.isEmpty() && triggersOfJob.get(0) != null) {
            scheduler.unscheduleJob(triggersOfJob.get(0).getKey());
        }
        JSONObject jsonObject = new JSONObject();
        JsonUtil.setTriggerToJson(jsonObject, triggersOfJob.stream().findFirst().orElse(null));
        return jsonObject;
    }

    /**
     * Returns map that has project name as key and map of study code to corresponding JobInfo as value
     */
    @LogMeAround
    public Map<String, Map<String, JobInfo>> getSchedulerInfo(String groupName) throws SchedulerException {
        Map<String, Map<String, JobInfo>> jobInfos = new HashMap<>();
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            String studyCode = jobDetail.getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
            String projectName = jobDetail.getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);

            JobInfo jobInfo = new JobInfo();
            jobInfo.studyCode = studyCode;
            jobInfo.projectName = projectName;
            jobInfo.jobKey = jobKey;

            jobInfos.putIfAbsent(projectName, new HashMap<>());
            jobInfos.get(projectName).put(studyCode, jobInfo);
        }
        return jobInfos;
    }

    @LogMeAround
    private Map<String, Map<String, JobInfo>> getQueuedJobs() throws SchedulerException {
        Map<String, Map<String, JobInfo>> jobInfos = new HashMap<>();
        Map<JobKey, JobExecutionEvent.ExecutionState> jobKeys = new HashMap<>();
        Set<String> keySet = schedulerStatusStorage.getStudyStatusesMap().keySet();
        for (String key : keySet) {
            Object value = schedulerStatusStorage.getStudyStatusesMap().get(key);
            if (value.equals(JobExecutionEvent.ExecutionState.QUEUED) || value.equals(JobExecutionEvent.ExecutionState.FIRED)) {
                jobKeys.put(Util.jobKeyFromString(key), (JobExecutionEvent.ExecutionState) (value));
            }
        }

        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKeys.containsKey(jobKey)) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    String studyCode = jobDetail.getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
                    String projectName = jobDetail.getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);

                    JobInfo jobInfo = new JobInfo();
                    jobInfo.studyCode = studyCode;
                    jobInfo.projectName = projectName;
                    jobInfo.jobKey = jobKey;
                    jobInfo.executionState = jobKeys.get(jobKey);
                    jobInfos.putIfAbsent(projectName, new HashMap<>());
                    jobInfos.get(projectName).put(studyCode, jobInfo);
                }
            }
        }
        return jobInfos;
    }

    private Map<String, List<String>> getJobRepositoryParamsFromInfo(Map<String, Map<String, JobInfo>> jobInfos) {
        Map<String, List<String>> studies = new HashMap<>();
        for (Map.Entry<String, Map<String, JobInfo>> entry : jobInfos.entrySet()) {
            String projectName = entry.getKey();
            for (Map.Entry<String, JobInfo> entry2 : entry.getValue().entrySet()) {
                studies.putIfAbsent(projectName, new ArrayList<>());
                studies.get(projectName).add(entry2.getKey());
            }
        }
        return studies;
    }

    @LogMeAround
    private Optional<JSONObject> getErrorObject(String projectName, String studyCode) throws JSONException, SchedulerException {
        Optional<JSONObject> jsonObject = getJobRunningObject(projectName, studyCode);
        if (jsonObject.isPresent()) {
            return jsonObject;
        }
        jsonObject = getJobQueuedObject(projectName, studyCode);
        return jsonObject;
    }

    @LogMeAround
    private Optional<JSONObject> getJobRunningObject(String projectName, String studyCode) throws JSONException {
        JobExecution latestJobExecution = functionsManager.getLatestJobExecution(projectName, studyCode, ETL_JOB_NAME);

        if (latestJobExecution != null && latestJobExecution.isRunning()) {
            JSONObject jsonObject = new JSONObject();
            JsonUtil.setJobExecutionToJson(jsonObject, latestJobExecution);
            JsonUtil.setError(jsonObject, "Job for project: " + projectName + ", study:" + studyCode + " is already running.");
            return Optional.of(jsonObject);
        }
        return Optional.empty();
    }

    @LogMeAround
    private Optional<JSONObject> getJobQueuedObject(String projectName, String studyCode) throws JSONException, SchedulerException {
        Map<String, Map<String, JobInfo>> queuedJobInfos = getQueuedJobs();
        final JobInfo jobInfo = queuedJobInfos.getOrDefault(projectName, new HashMap<>()).get(studyCode);
        if (jobInfo != null) {
            JSONObject jsonObject = new JSONObject();
            JsonUtil.setSchedulerJobExecutionToJson(jsonObject, jobInfo.getExecutionState());
            JsonUtil.setError(jsonObject, "Job for project: " + projectName + ", study:" + studyCode + " is already queued.");
            return Optional.of(jsonObject);
        }
        return Optional.empty();
    }

    public boolean isStudyScheduled(String studyCode, String drugProgramme) throws SchedulerException {
        Map<String, Map<String, JobInfo>> jobInfos = getSchedulerInfo(ETL_GROUP_NAME);
        List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobInfos.get(studyCode).get(drugProgramme).jobKey);
        return triggersOfJob.stream()
                .anyMatch(trigger -> trigger.getNextFireTime() != null);
    }
}

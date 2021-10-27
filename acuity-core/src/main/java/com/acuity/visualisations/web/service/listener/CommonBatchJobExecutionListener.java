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

package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public abstract class CommonBatchJobExecutionListener extends JobExecutionListenerSupport implements ExecutionStateAware {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ObservableListener observable;
    private final Scheduler scheduler;
    private SchedulerStatusStorage schedulerStatusStorage;

    public CommonBatchJobExecutionListener(ObservableListener observable, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        this.observable = observable;
        this.scheduler = scheduler;
        this.schedulerStatusStorage = schedulerStatusStorage;
    }

    @Override
    @LogMeAround("Listener")
    public void beforeJob(JobExecution jobExecution) {
        BatchJobExecutionKey key = new BatchJobExecutionKey(jobExecution);
        JobExecutionEvent event = new JobExecutionEvent(key, getExecutionState());
        try {
            refreshSchedulerContext(jobExecution);
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage());
        }
        observable.processEvent(event);
    }

    @LogMeAround("Listener")
    private void refreshSchedulerContext(JobExecution jobExecution) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                String studyCode = jobDetail.getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
                String projectName = jobDetail.getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);

                String targetStudyCode = jobExecution.getJobParameters().getString(JobLauncherConsts.STUDY_KEY);
                String targetProjectName = jobExecution.getJobParameters().getString(JobLauncherConsts.PROJECT_KEY);
                if (projectName.equals(targetProjectName) && studyCode.equals(targetStudyCode)) {
                    schedulerStatusStorage.getStudyStatusesMap()
                            .put(Util.jobKeyToString(jobKey), getExecutionState());
                }
            }
        }
    }

}

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

import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.EtlSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

@Slf4j
public class EtlQuartzJobListener extends CommonQuartzJobListener {

    private boolean amlEnabledGlobally;
    private EtlSchedulerService etlSchedulerService;

    public EtlQuartzJobListener(ObservableListener observable, EtlSchedulerService etlSchedulerService, boolean amlEnabledGlobally) {
        super(observable);
        this.amlEnabledGlobally = amlEnabledGlobally;
        this.etlSchedulerService = etlSchedulerService;
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        triggerAml(context);
        super.jobWasExecuted(context, jobException);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.FINISHED;
    }

    private void triggerAml(JobExecutionContext context) {
        if (!amlEnabledGlobally) {
            return;
        }
        final boolean amlEnabledForStudy = context.getJobDetail().getJobDataMap().getBoolean(JobLauncherConsts.AML_ENABLED);
        if (amlEnabledForStudy) {
            String projectName = context.getJobDetail().getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);
            String studyCode = context.getJobDetail().getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
            try {
                etlSchedulerService.triggerAmlJob(projectName, studyCode);
            } catch (SchedulerException e) {
                log.error("Cannot trigger AML job for dataset '" + studyCode + "'. Exception: " + e.getMessage());
            }
        }
    }

}

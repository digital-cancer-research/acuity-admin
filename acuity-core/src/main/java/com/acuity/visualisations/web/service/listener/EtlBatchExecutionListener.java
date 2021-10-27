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

import com.acuity.visualisations.batch.reader.SkippedFilesAndHoldersAware;
import com.acuity.visualisations.batch.tasklet.PreprocessResourcesTasklet;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.util.LoggerDecorator;
import com.acuity.visualisations.web.service.EtlSchedulerService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

@Component("jobListener")
public class EtlBatchExecutionListener implements JobExecutionListener {

    private static final LoggerDecorator LOGGER = new LoggerDecorator(EtlBatchExecutionListener.class);
    public static final String ETL_SKIPPED = "NO CHANGES - ETL SKIPPED";

    @Autowired
    private EtlSchedulerService etlSchedulerService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Long jobExecutionId = jobExecution.getId();
        String jobName = jobExecution.getJobInstance().getJobName();
        String studyName = jobExecution.getJobParameters().getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobExecution.getJobParameters().getString(JobLauncherConsts.PROJECT_KEY);
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, "Spring batch job started");

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        for (StepExecution execution : stepExecutions) {
            if (execution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                jobExecution.setStatus(BatchStatus.FAILED);
                jobExecution.setExitStatus(ExitStatus.FAILED);
                break;
            }
            if (execution.getExitStatus().getExitCode().equals(PreprocessResourcesTasklet.SKIP)) {
                jobExecution.setExitStatus(new ExitStatus(ETL_SKIPPED));
                break;
            }
            if (execution.getExitStatus().getExitCode().equals(SkippedFilesAndHoldersAware.COMPLETED_WITH_SKIPS)) {
                jobExecution.setExitStatus(new ExitStatus(SkippedFilesAndHoldersAware.COMPLETED_WITH_SKIPS, execution.getExitStatus()
                        .getExitDescription()));
            }
        }
        Long jobExecutionId = jobExecution.getId();
        String jobName = jobExecution.getJobInstance().getJobName();
        String studyName = jobExecution.getJobParameters().getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobExecution.getJobParameters().getString(JobLauncherConsts.PROJECT_KEY);
        Date startTime = jobExecution.getStartTime();
        Date endTime = jobExecution.getEndTime();
        String exitCode = jobExecution.getExitStatus().getExitCode();
        LOGGER.info(jobExecutionId, jobName, projectName, studyName,
                "Spring batch job finished Start time: {}, End Time: {}, Exit Code: {}", new Object[]{startTime, endTime, exitCode});
    }


}

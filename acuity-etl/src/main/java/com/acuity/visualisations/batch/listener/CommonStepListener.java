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

package com.acuity.visualisations.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.util.LoggerDecorator;

@Component("stepListener")
public class CommonStepListener implements StepExecutionListener {

    private static final LoggerDecorator LOGGER = new LoggerDecorator(CommonStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Long jobExecutionId = stepExecution.getJobExecution().getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String studyName = jobParameters.getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobParameters.getString(JobLauncherConsts.PROJECT_KEY);
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, "Step started, Step Name: {}",
                new Object[]{stepExecution.getStepName()});
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Long jobExecutionId = stepExecution.getJobExecution().getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String studyName = jobParameters.getString(JobLauncherConsts.STUDY_KEY);
        String projectName = jobParameters.getString(JobLauncherConsts.PROJECT_KEY);
        LOGGER.info(jobExecutionId, jobName, projectName, studyName, "Step finished, Step Name: {}, Step Status: {}", new Object[]{
                stepExecution.getStepName(), stepExecution.getStatus()});
        return stepExecution.getExitStatus();
    }

}

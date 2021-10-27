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

package com.acuity.visualisations.task;

import com.acuity.visualisations.util.JobLauncherConsts;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JobLauncherDetails extends QuartzJobBean {

    static final String JOB_NAME = "jobName";

    private static Logger log = LoggerFactory.getLogger(JobLauncherDetails.class);

    private JobLocator jobLocator;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> jobDataMap = context.getMergedJobDataMap();
        String jobName = (String) jobDataMap.get(JOB_NAME);
        jobDataMap.put(JobLauncherConsts.UNIQUE_KEY, context.get(JobLauncherConsts.UNIQUE_KEY));
        JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap);
        try {
            validateConcurrentRun(jobName, jobParameters);
            JobParameters displayJobParameters = getDisplayJobParameters(jobParameters);
            log.info("Quartz trigger firing with Spring Batch jobName={} and jobParamterers={}", jobName, displayJobParameters);

            jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
        } catch (CannotSerializeTransactionException e) {
            throw new JobExecutionException("Job for project: " + jobParameters.getString(JobLauncherConsts.PROJECT_KEY) + ", study:"
                    + jobParameters.getString(JobLauncherConsts.STUDY_KEY) + " wasn't launched. Job repository is currently overloaded.");
        } catch (JobExecutionAlreadyRunningException e) {
            throw new JobExecutionException("Job for project: " + jobParameters.getString(JobLauncherConsts.PROJECT_KEY) + ", study:"
                    + jobParameters.getString(JobLauncherConsts.STUDY_KEY) + " is already running.", e);
        } catch (Exception e) {
            throw new JobExecutionException("Couldn't execute job project: " + jobParameters.getString(JobLauncherConsts.PROJECT_KEY)
                    + ", study:" + jobParameters.getString(JobLauncherConsts.STUDY_KEY) + ". Exception message" + e.getMessage(), e);
        }
    }

    private JobParameters getDisplayJobParameters(JobParameters jobParameters) {
        return new JobParametersBuilder().addString(JobLauncherConsts.PROJECT_KEY, jobParameters.getString(JobLauncherConsts.PROJECT_KEY))
                .addString(JobLauncherConsts.STUDY_KEY, jobParameters.getString(JobLauncherConsts.STUDY_KEY)).toJobParameters();
    }

    private void validateConcurrentRun(String jobName, JobParameters jobParameters) throws JobExecutionAlreadyRunningException {
        Set<JobExecution> jobs = jobExplorer.findRunningJobExecutions(jobName);
        for (JobExecution job : jobs) {
            if (job.isRunning() && jobParametersEquals(jobParameters, job.getJobParameters())) {
                throw new JobExecutionAlreadyRunningException("A job for this job is already running: " + job);
            }
        }
    }

    private boolean jobParametersEquals(JobParameters jobParameters1, JobParameters jobParameters2) {
        String studyCode1 = jobParameters1.getString(JobLauncherConsts.STUDY_KEY);
        String projectName1 = jobParameters1.getString(JobLauncherConsts.PROJECT_KEY);
        String studyCode2 = jobParameters2.getString(JobLauncherConsts.STUDY_KEY);
        String projectName2 = jobParameters2.getString(JobLauncherConsts.PROJECT_KEY);
        return StringUtils.equals(studyCode1, studyCode2) && StringUtils.equals(projectName1, projectName2);
    }

    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

        JobParametersBuilder builder = new JobParametersBuilder();

        for (Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && !key.equals(JOB_NAME)) {
                builder.addString(key, (String) value);
            } else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number) value).longValue());
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            } else if (value instanceof Boolean) {
                builder.addString(key, value.toString());
            } else if (!key.equals(JOB_NAME)) {
                log.debug("JobDataMap contains values which are not job parameters (ignoring).");

            }
        }
        return builder.toJobParameters();
    }

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

}

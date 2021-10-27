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

package com.acuity.visualisations.web.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobExecution;

import com.acuity.visualisations.util.JobLauncherConsts;

public class BatchJobExecutionKey {

    private String projectName;
    private String studyCode;
    private String jobExecutionGuid;

    public BatchJobExecutionKey(String projectName, String studyCode, String jobExecutionGuid) {
        this.projectName = projectName;
        this.studyCode = studyCode;
        this.jobExecutionGuid = jobExecutionGuid;
    }

    public BatchJobExecutionKey(JobExecutionContext context) {
        this.projectName = context.getJobDetail().getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);
        this.studyCode = context.getJobDetail().getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
        this.jobExecutionGuid = (String) context.get(JobLauncherConsts.UNIQUE_KEY);
    }

    public BatchJobExecutionKey(JobExecution jobExecution) {
        this.studyCode = jobExecution.getJobParameters().getString(JobLauncherConsts.STUDY_KEY);
        this.projectName = jobExecution.getJobParameters().getString(JobLauncherConsts.PROJECT_KEY);
        this.jobExecutionGuid = jobExecution.getJobParameters().getString(JobLauncherConsts.UNIQUE_KEY);
    }

    public BatchJobExecutionKey(JobDetail jobDetail) {
        this.studyCode = jobDetail.getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
        this.projectName = jobDetail.getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);
        this.jobExecutionGuid = null;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getStudyCode() {
        return studyCode;
    }

    public String getJobExecutionGuid() {
        return jobExecutionGuid;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(projectName).append(studyCode).append(jobExecutionGuid).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        BatchJobExecutionKey key = (BatchJobExecutionKey) obj;
        return new EqualsBuilder().append(projectName, key.projectName).append(studyCode, key.studyCode)
                .append(jobExecutionGuid, key.jobExecutionGuid).isEquals();
    }

}

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

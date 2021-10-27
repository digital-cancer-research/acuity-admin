package com.acuity.visualisations.batch.holders;

public class JobExecutionInfoBuilder {

    private Long jobExecutionId;
    private String jobName;

    private String projectName;
    private String studyName;
    private String uniqueKey;

    public JobExecutionInfoBuilder setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
        return this;
    }

    public JobExecutionInfoBuilder setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public JobExecutionInfoBuilder setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public JobExecutionInfoBuilder setStudyName(String studyName) {
        this.studyName = studyName;
        return this;
    }

    public JobExecutionInfoBuilder setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
        return this;
    }

    public JobExecutionInfo build() {
        JobExecutionInfo info = new JobExecutionInfo();
        info.setJobExecutionId(jobExecutionId);
        info.setJobName(jobName);
        info.setProjectName(projectName);
        info.setStudyName(studyName);
        info.setUniqueKey(uniqueKey);
        return info;
    }

}

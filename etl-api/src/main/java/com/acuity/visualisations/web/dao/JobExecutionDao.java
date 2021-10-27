package com.acuity.visualisations.web.dao;

import org.springframework.batch.core.JobExecution;

import java.util.List;
import java.util.Map;

public interface JobExecutionDao {

    List<JobExecution> getJobExecutions(String projectName, String studyCode);

    Map<String, Map<String, List<JobExecution>>> getJobExecutions(Map<String, List<String>> keys);

    JobExecution getLatestJobExecution(String projectName, String studyCode, String jobName);

    Map<String, Map<String, JobExecution>> getLatestJobExecution(Map<String, List<String>> keys, String jobName);

}

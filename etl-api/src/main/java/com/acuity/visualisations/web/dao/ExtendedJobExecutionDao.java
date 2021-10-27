package com.acuity.visualisations.web.dao;

import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import org.springframework.batch.core.JobExecution;

import java.util.List;
import java.util.Map;

public interface ExtendedJobExecutionDao extends JobExecutionDao {

    /**
     * Gets the batch job execution IDs for a given study
     *
     * @param studyCode The clinical study code
     * @return A list of Job Execution IDs for the given study
     */
    List<Integer> getJobExecutionsByStudy(String studyCode);

    Map<BatchJobExecutionKey, JobExecution> getLatestJobExecutionByFullKeyAndJobName(List<BatchJobExecutionKey> keys, String jobName);

    List<String> getFailedStudies();

    List<String> getNotRunStudies();

    Map<String, List<String>> getScheduledStudiesFilenames();

    void insertScheduledCleanEtlStatus(String status, Long jobId);
}

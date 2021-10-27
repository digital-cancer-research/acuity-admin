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

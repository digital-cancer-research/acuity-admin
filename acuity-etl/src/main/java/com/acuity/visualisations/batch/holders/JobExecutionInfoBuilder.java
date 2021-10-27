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

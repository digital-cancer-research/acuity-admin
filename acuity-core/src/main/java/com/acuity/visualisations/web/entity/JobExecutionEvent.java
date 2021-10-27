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

import com.google.common.collect.Sets;
import lombok.ToString;
import org.quartz.JobExecutionException;

import java.util.Set;
import java.util.UUID;

import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.AML_FINISHED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.AML_FIRED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.AML_QUEUED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.AML_STARTED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.FINISHED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.FIRED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.QUEUED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.STARTED;
import static com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState.STUDY_IN_DATABASE;

@ToString(of = "executionState")
public class JobExecutionEvent {

    public enum ExecutionState {
        QUEUED("QUEUED"),
        FIRED("FIRED"),
        STARTED("STARTED"),
        STUDY_IN_DATABASE("STUDY IN DATABASE"),
        FINISHED("COMPLETED"),

        AML_FIRED("FIRED"),
        AML_QUEUED("QUEUED"),
        AML_STARTED("STARTED"),
        AML_FINISHED("COMPLETED");


        private String displayName;

        ExecutionState(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Set<ExecutionState> runningStates = Sets.newHashSet(QUEUED, FIRED, STARTED);
    private Set<ExecutionState> amlRunningStates = Sets.newHashSet(AML_FIRED, AML_QUEUED, AML_STARTED, AML_FINISHED);

    private BatchJobExecutionKey executionKey;
    private String executionId;
    private ExecutionState executionState;
    private JobExecutionException exception;

    public JobExecutionEvent(BatchJobExecutionKey executionKey, ExecutionState executionState) {
        this.executionKey = executionKey;
        this.executionId = UUID.randomUUID().toString().replaceAll("-", "");
        this.executionState = executionState;
    }

    public JobExecutionEvent(BatchJobExecutionKey executionKey, ExecutionState executionState, JobExecutionException exception) {
        this.executionKey = executionKey;
        this.executionState = executionState;
        this.exception = exception;
        this.executionId = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public BatchJobExecutionKey getExecutionKey() {
        return executionKey;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

    public JobExecutionException getException() {
        return exception;
    }

    public boolean isQueued() {
        return executionState.equals(QUEUED);
    }

    public boolean isFired() {
        return executionState.equals(FIRED);
    }

    public boolean isStarted() {
        return executionState.equals(STARTED);
    }

    public boolean isFinished() {
        return executionState.equals(FINISHED);
    }

    public boolean isStudyInDatabase() {
        return executionState.equals(STUDY_IN_DATABASE);
    }

    public boolean isInEtlRunningState() {
        return runningStates.contains(executionState);
    }

    public boolean isInAmlRunningState() {
        return amlRunningStates.contains(executionState);
    }
}

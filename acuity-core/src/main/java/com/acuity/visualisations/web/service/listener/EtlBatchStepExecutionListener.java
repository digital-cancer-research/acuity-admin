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

package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

public class EtlBatchStepExecutionListener extends CommonBatchStepExecutionListener {

    public EtlBatchStepExecutionListener(ObservableListener observable) {
        super(observable);
    }

    @Override
    @LogMeAround("Listener")
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        BatchJobExecutionKey key = new BatchJobExecutionKey(jobExecution);
        JobExecutionEvent event = new JobExecutionEvent(key, JobExecutionEvent.ExecutionState.STUDY_IN_DATABASE);
        getObservable().processEvent(event);
        return stepExecution.getExitStatus();
    }
}

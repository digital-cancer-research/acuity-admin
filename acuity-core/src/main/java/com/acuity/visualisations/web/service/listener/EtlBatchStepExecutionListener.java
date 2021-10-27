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

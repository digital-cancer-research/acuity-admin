package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.Scheduler;
import org.springframework.batch.core.JobExecution;

public class EtlBatchJobExecutionListener extends CommonBatchJobExecutionListener {

    public EtlBatchJobExecutionListener(ObservableListener observable, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        super(observable, scheduler, schedulerStatusStorage);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.STARTED;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);
    }
}

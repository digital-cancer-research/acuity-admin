package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.Scheduler;

public class EtlQuartzSchedulerListener extends CommonQuartzSchedulerListener {

    public EtlQuartzSchedulerListener(ObservableListener observable, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        super(observable, scheduler, schedulerStatusStorage);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.QUEUED;
    }

}

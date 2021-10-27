package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;

// Quartz Trigger Listener
public class EtlQuartzTriggerListener extends CommonQuartzTriggerListener {

    public EtlQuartzTriggerListener(ObservableListener observable, SchedulerStatusStorage schedulerStatusStorage) {
        super(observable, schedulerStatusStorage);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.FIRED;
    }

}

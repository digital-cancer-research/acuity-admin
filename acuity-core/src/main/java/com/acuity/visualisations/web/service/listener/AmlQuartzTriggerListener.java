package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;

public class AmlQuartzTriggerListener extends CommonQuartzTriggerListener {

    public AmlQuartzTriggerListener(ObservableListener observable, SchedulerStatusStorage schedulerStatusStorage) {
        super(observable, schedulerStatusStorage);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.AML_FIRED;
    }

}

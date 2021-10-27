package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;

public class AmlQuartzJobListener extends CommonQuartzJobListener {

    public AmlQuartzJobListener(ObservableListener observable) {
        super(observable);
    }

    @Override
    public ExecutionState getExecutionState() {
        return ExecutionState.AML_FINISHED;
    }

}

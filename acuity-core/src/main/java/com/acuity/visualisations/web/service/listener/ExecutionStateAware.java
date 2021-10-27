package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;

public interface ExecutionStateAware {

    ExecutionState getExecutionState();
}

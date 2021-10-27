package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

public class CommonBatchStepExecutionListener extends StepExecutionListenerSupport {

    @Getter(AccessLevel.PROTECTED)
    private ObservableListener observable;

    public CommonBatchStepExecutionListener(ObservableListener observable) {
        this.observable = observable;
    }

    @Override
    @LogMeAround("Listener")
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}

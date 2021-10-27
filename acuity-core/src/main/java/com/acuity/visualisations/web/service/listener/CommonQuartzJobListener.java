package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

public abstract class CommonQuartzJobListener extends JobListenerSupport implements ExecutionStateAware {

    private ObservableListener observable;

    public CommonQuartzJobListener(ObservableListener observable) {
        this.observable = observable;
    }

    @Override
    public String getName() {
        return getClass().getCanonicalName();
    }

    @Override
    @LogMeAround("Listener")
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        BatchJobExecutionKey key = new BatchJobExecutionKey(context);
        JobExecutionEvent event = new JobExecutionEvent(key, getExecutionState(), jobException);
        observable.processEvent(event);
    }
}

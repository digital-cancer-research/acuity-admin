package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

import java.util.UUID;

public abstract class CommonQuartzTriggerListener extends TriggerListenerSupport implements ExecutionStateAware {

    private ObservableListener observable;
    private SchedulerStatusStorage schedulerStatusStorage;

    public CommonQuartzTriggerListener(ObservableListener observable, SchedulerStatusStorage schedulerStatusStorage) {
        this.observable = observable;
        this.schedulerStatusStorage = schedulerStatusStorage;
    }

    @Override
    public String getName() {
        return getClass().getCanonicalName();
    }

    @Override
    @LogMeAround("Listener")
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        String jobExecutionGuid = UUID.randomUUID().toString().replaceAll("-", "");
        context.put(JobLauncherConsts.UNIQUE_KEY, jobExecutionGuid);
        BatchJobExecutionKey key = new BatchJobExecutionKey(context);
        final ExecutionState executionState = getExecutionState();
        JobExecutionEvent event = new JobExecutionEvent(key, executionState);
        schedulerStatusStorage.getStudyStatusesMap()
                .put(Util.jobKeyToString(context.getJobDetail().getKey()), executionState);
        observable.processEvent(event);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

}

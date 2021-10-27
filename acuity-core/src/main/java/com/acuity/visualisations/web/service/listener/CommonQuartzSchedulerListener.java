package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.entity.JobExecutionEvent.ExecutionState;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonQuartzSchedulerListener extends SchedulerListenerSupport implements ExecutionStateAware {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ObservableListener observable;

    private Scheduler scheduler;
    private SchedulerStatusStorage schedulerStatusStorage;

    public CommonQuartzSchedulerListener(ObservableListener observable, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        this.observable = observable;
        this.schedulerStatusStorage = schedulerStatusStorage;
        this.scheduler = scheduler;
    }

    @Override
    @LogMeAround("Listener")
    public void jobScheduled(Trigger trigger) {
        JobDetail jobDetail = null;
        final ExecutionState executionState = getExecutionState();
        try {
            jobDetail = scheduler.getJobDetail(trigger.getJobKey());
            schedulerStatusStorage.getStudyStatusesMap()
                    .put(Util.jobKeyToString(jobDetail.getKey()), executionState);
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage());
        }
        if (jobDetail == null) {
            return;
        }
        BatchJobExecutionKey key = new BatchJobExecutionKey(jobDetail);
        JobExecutionEvent event = new JobExecutionEvent(key, executionState);
        observable.processEvent(event);
    }
}

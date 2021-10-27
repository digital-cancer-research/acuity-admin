/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

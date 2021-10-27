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
import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Observable;

public abstract class AllEventListener extends Observable implements JobListener, TriggerListener, InitializingBean, JobExecutionListener,
        StepExecutionListener, SchedulerListener, ObservableListener, ConditionalListener {

    @Autowired
    @Qualifier("schedulerFactory")
    private Scheduler scheduler;

    @Autowired
    private SchedulerStatusStorage schedulerStatusStorage;

    private CommonQuartzJobListener jobListener;
    private CommonQuartzTriggerListener triggerListener;
    private CommonBatchJobExecutionListener jobExecutionListener;
    private CommonBatchStepExecutionListener stepExecutionListener;
    private CommonQuartzSchedulerListener schedulerListener;

    public void afterPropertiesSet() throws Exception {
        this.jobListener = getJobListener(this);
        this.triggerListener = getTriggerListener(this, schedulerStatusStorage);
        this.jobExecutionListener = getJobExecutionListener(this, scheduler, schedulerStatusStorage);
        this.stepExecutionListener = getStepExecutionListener(this);
        this.schedulerListener = getSchedulerListener(this, scheduler, schedulerStatusStorage);
        scheduler.getListenerManager().addJobListener(this);
        scheduler.getListenerManager().addTriggerListener(this);
        scheduler.getListenerManager().addSchedulerListener(this);
    }

    abstract CommonQuartzJobListener getJobListener(AllEventListener allEventListener);

    abstract CommonBatchJobExecutionListener getJobExecutionListener(AllEventListener allEventListener,
                                                                     Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage);

    abstract CommonBatchStepExecutionListener getStepExecutionListener(AllEventListener allEventListener);

    abstract CommonQuartzTriggerListener getTriggerListener(AllEventListener allEventListener,
                                                            SchedulerStatusStorage schedulerStatusStorage);

    abstract CommonQuartzSchedulerListener getSchedulerListener(AllEventListener allEventListener, Scheduler scheduler,
                                                                SchedulerStatusStorage schedulerStatusStorage);

    @Override
    @LogMeAround("Listener")
    public void processEvent(JobExecutionEvent event) {
        setChanged();
        notifyObservers(event);
        clearChanged();
    }

    @Override
    public String getName() {
        return getClass().getCanonicalName();
    }

    @Override
    @LogMeAround("Listener")
    public void jobToBeExecuted(JobExecutionContext context) {
        jobListener.jobToBeExecuted(context);
    }

    @Override
    @LogMeAround("Listener")
    public void jobExecutionVetoed(JobExecutionContext context) {
        jobListener.jobExecutionVetoed(context);
    }

    @Override
    @LogMeAround("Listener")
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isTriggerApplicable(context.getTrigger())) {
            jobListener.jobWasExecuted(context, jobException);
        }
    }

    @Override
    @LogMeAround("Listener")
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        if (isTriggerApplicable(trigger)) {
            triggerListener.triggerFired(trigger, context);

        }
    }

    @Override
    @LogMeAround("Listener")
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return triggerListener.vetoJobExecution(trigger, context);
    }

    @Override
    @LogMeAround("Listener")
    public void triggerMisfired(Trigger trigger) {
        triggerListener.triggerMisfired(trigger);
    }

    @Override
    @LogMeAround("Listener")
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        triggerListener.triggerComplete(trigger, context, triggerInstructionCode);
    }

    @Override
    @LogMeAround("Listener")
    public void beforeJob(JobExecution jobExecution) {
        jobExecutionListener.beforeJob(jobExecution);
    }

    @Override
    @LogMeAround("Listener")
    public void afterJob(JobExecution jobExecution) {
        jobExecutionListener.afterJob(jobExecution);
    }

    @Override
    @LogMeAround("Listener")
    public void beforeStep(StepExecution stepExecution) {
        stepExecutionListener.beforeStep(stepExecution);

    }

    @Override
    @LogMeAround("Listener")
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecutionListener.afterStep(stepExecution);
    }

    @Override
    @LogMeAround("Listener")
    public void jobScheduled(Trigger trigger) {
        if (isTriggerApplicable(trigger)) {
            schedulerListener.jobScheduled(trigger);
        }
    }

    @Override
    @LogMeAround("Listener")
    public void jobUnscheduled(TriggerKey triggerKey) {
        schedulerListener.jobUnscheduled(triggerKey);
    }

    @Override
    @LogMeAround("Listener")
    public void triggerFinalized(Trigger trigger) {
        schedulerListener.triggerFinalized(trigger);
    }

    @Override
    @LogMeAround("Listener")
    public void triggerPaused(TriggerKey triggerKey) {
        schedulerListener.triggerPaused(triggerKey);
    }

    @Override
    @LogMeAround("Listener")
    public void triggersPaused(String triggerGroup) {
        schedulerListener.triggersPaused(triggerGroup);
    }

    @Override
    @LogMeAround("Listener")
    public void triggerResumed(TriggerKey triggerKey) {
        schedulerListener.triggerResumed(triggerKey);
    }

    @Override
    @LogMeAround("Listener")
    public void triggersResumed(String triggerGroup) {
        schedulerListener.triggersResumed(triggerGroup);
    }

    @Override
    @LogMeAround("Listener")
    public void jobAdded(JobDetail jobDetail) {
        schedulerListener.jobAdded(jobDetail);
    }

    @Override
    @LogMeAround("Listener")
    public void jobDeleted(JobKey jobKey) {
        schedulerListener.jobDeleted(jobKey);
    }

    @Override
    @LogMeAround("Listener")
    public void jobPaused(JobKey jobKey) {
        schedulerListener.jobPaused(jobKey);
    }

    @Override
    @LogMeAround("Listener")
    public void jobsPaused(String jobGroup) {
        schedulerListener.jobsPaused(jobGroup);
    }

    @Override
    @LogMeAround("Listener")
    public void jobResumed(JobKey jobKey) {
        schedulerListener.jobResumed(jobKey);
    }

    @Override
    @LogMeAround("Listener")
    public void jobsResumed(String jobGroup) {
        schedulerListener.jobsResumed(jobGroup);
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerError(String msg, SchedulerException cause) {
        schedulerListener.schedulerError(msg, cause);
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerInStandbyMode() {
        schedulerListener.schedulerInStandbyMode();
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerStarted() {
        schedulerListener.schedulerStarted();
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerStarting() {
        schedulerListener.schedulerStarting();
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerShutdown() {
        schedulerListener.schedulerShutdown();
    }

    @Override
    @LogMeAround("Listener")
    public void schedulerShuttingdown() {
        schedulerListener.schedulerShuttingdown();
    }

    @Override
    @LogMeAround("Listener")
    public void schedulingDataCleared() {
        schedulerListener.schedulingDataCleared();
    }

}

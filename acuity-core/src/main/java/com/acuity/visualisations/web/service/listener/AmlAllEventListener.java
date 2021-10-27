package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import static com.acuity.visualisations.util.JobLauncherConsts.AML_GROUP_NAME;

@Component("amlGlobalListener")
public class AmlAllEventListener extends AllEventListener {

    @Override
    CommonQuartzJobListener getJobListener(AllEventListener allEventListener) {
        return new AmlQuartzJobListener(this);
    }

    @Override
    CommonBatchJobExecutionListener getJobExecutionListener(AllEventListener allEventListener, Scheduler scheduler,
                                                            SchedulerStatusStorage schedulerStatusStorage) {
        return new AmlBatchJobExecutionListener(this, scheduler, schedulerStatusStorage);
    }

    @Override
    CommonBatchStepExecutionListener getStepExecutionListener(AllEventListener allEventListener) {
        return new CommonBatchStepExecutionListener(this);
    }

    @Override
    CommonQuartzTriggerListener getTriggerListener(AllEventListener allEventListener, SchedulerStatusStorage schedulerStatusStorage) {
        return new AmlQuartzTriggerListener(this, schedulerStatusStorage);
    }

    @Override
    CommonQuartzSchedulerListener getSchedulerListener(AllEventListener allEventListener, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        return new AmlQuartzSchedulerListener(this, scheduler, schedulerStatusStorage);
    }

    @Override
    public boolean isTriggerApplicable(Trigger trigger) {
        return AML_GROUP_NAME.equals(trigger.getJobKey().getGroup());
    }
}

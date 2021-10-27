package com.acuity.visualisations.web.service.listener;

import com.acuity.visualisations.web.service.EtlSchedulerService;
import com.acuity.visualisations.web.service.SchedulerStatusStorage;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.acuity.visualisations.util.JobLauncherConsts.ETL_GROUP_NAME;

@Component("globalListener")
public class EtlAllEventListener extends AllEventListener {

    @Value("${azureml.enable:false}")
    private boolean amlEnabledGlobally;
    @Autowired
    private EtlSchedulerService etlSchedulerService;

    @Override
    EtlQuartzJobListener getJobListener(AllEventListener allEventListener) {
        return new EtlQuartzJobListener(this, etlSchedulerService, amlEnabledGlobally);
    }

    @Override
    EtlBatchJobExecutionListener getJobExecutionListener(AllEventListener allEventListener, Scheduler scheduler,
                                                         SchedulerStatusStorage schedulerStatusStorage) {
        return new EtlBatchJobExecutionListener(this, scheduler, schedulerStatusStorage);
    }

    @Override
    CommonBatchStepExecutionListener getStepExecutionListener(AllEventListener allEventListener) {
        return new EtlBatchStepExecutionListener(this);
    }

    @Override
    CommonQuartzTriggerListener getTriggerListener(AllEventListener allEventListener, SchedulerStatusStorage schedulerStatusStorage) {
        return new EtlQuartzTriggerListener(this, schedulerStatusStorage);
    }

    @Override
    CommonQuartzSchedulerListener getSchedulerListener(AllEventListener allEventListener, Scheduler scheduler, SchedulerStatusStorage schedulerStatusStorage) {
        return new EtlQuartzSchedulerListener(this, scheduler, schedulerStatusStorage);
    }

    @Override
    public boolean isTriggerApplicable(Trigger trigger) {
        return ETL_GROUP_NAME.equals(trigger.getJobKey().getGroup());
    }
}

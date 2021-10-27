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

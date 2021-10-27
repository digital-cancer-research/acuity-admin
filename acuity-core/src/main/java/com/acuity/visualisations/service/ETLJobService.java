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

package com.acuity.visualisations.service;

import com.acuity.visualisations.exception.MethodFailureException;
import com.acuity.visualisations.task.DurableJobDetailFactoryBean;
import com.acuity.visualisations.util.ETLStudyRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import static com.acuity.visualisations.util.JobLauncherConsts.ETL_GROUP_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.ETL_JOB_NAME;
import static java.util.stream.Collectors.toMap;

@Service
public class ETLJobService extends AbstractJobService {
    protected static final Log LOGGER = LogFactory.getLog(ETLJobService.class);

    @Value("${acuity.defaultCronExpression}")
    private String defaultCronExpression;
    @Value("${acuity.timezone}")
    private String timezone;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        LOGGER.debug("ETLJobService " + this.toString());
    }

    @Override
    public void refreshJobList(Scheduler scheduler) {
        super.refreshJobList(scheduler);

        Map<String, DurableJobDetailFactoryBean> jobListTriggered = getStudyRules().stream()
                .filter(studyRule -> studyRule.isScheduled() && environment.acceptsProfiles("!local")
                        || studyRule.isScheduledClean() && !studyRule.isScheduled())
                .map(this::getDurableJobDetailFactoryBean)
                .collect(toMap(item -> getTriggerName(item.getJobDataMap().getString("etl.study")),
                        Function.identity()));
        try {
            for (TriggerKey key : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(ETL_GROUP_NAME))) {
                if (!jobListTriggered.containsKey(key.getName())) {
                    Trigger trigger = scheduler.getTrigger(key);
                    scheduler.unscheduleJob(trigger.getKey());
                    scheduler.deleteJob(trigger.getJobKey());
                }
            }

            for (Map.Entry<String, DurableJobDetailFactoryBean> detail : jobListTriggered.entrySet()) {
                final String beanName = detail.getKey();
                final DurableJobDetailFactoryBean bean = detail.getValue();
                scheduleJob(scheduler, bean, beanName);
            }
        } catch (SchedulerException e) {
            throw new MethodFailureException(e);
        }
    }

    private void scheduleJob(Scheduler scheduler, DurableJobDetailFactoryBean bean, String beanName) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(beanName);
        boolean triggerExists = scheduler.checkExists(triggerKey);
        CronTriggerFactoryBean trigger = getCronTriggerFactoryBean(bean, beanName);

        if (triggerExists) {
            scheduler.rescheduleJob(triggerKey, trigger.getObject());
        } else {
            try {
                scheduler.scheduleJob(trigger.getObject());
            } catch (ObjectAlreadyExistsException ex) {
                scheduler.rescheduleJob(triggerKey, trigger.getObject());
            }
        }
    }

    private CronTriggerFactoryBean getCronTriggerFactoryBean(DurableJobDetailFactoryBean bean, String beanName) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setTimeZone(TimeZone.getTimeZone(timezone));
        trigger.setJobDetail(bean.getObject());
        trigger.setGroup(getGroupName());
        trigger.setCronExpression(bean.getJobDataMap().getString("etl.cronExpression"));
        trigger.setBeanName(beanName);

        try {
            trigger.afterPropertiesSet();
        } catch (ParseException e) {
            throw new MethodFailureException(e);
        }
        return trigger;
    }

    @Override
    String getGroupName() {
        return ETL_GROUP_NAME;
    }

    @Override
    String getJobInstanceName() {
        return ETL_JOB_NAME;
    }

    private static String getTriggerName(String studyCode) {
        return "cronTrigger_" + studyCode;
    }

    @Override
    void populateJobDetailFactoryBeanWithProperties(ETLStudyRule studyRule, Map<String, Object> jobDataAsMap) {
        if (studyRule.isScheduledClean() && !studyRule.isScheduled()) {
            LocalDate nextEtlRunDate = LocalDate.now().plusDays(1);
            String cronExpression = String.format("%s %s %s %s %s %s %s", 0, 0, 3, nextEtlRunDate.getDayOfMonth(),
                    nextEtlRunDate.getMonth().getValue(), "?", nextEtlRunDate.getYear());
            studyRule.setCronExpression(cronExpression);
        }
        jobDataAsMap.put("etl.cronExpression",
                studyRule.getCronExpression() != null && !studyRule.getCronExpression().isEmpty() ? studyRule.getCronExpression()
                        : defaultCronExpression);
        jobDataAsMap.put("etl.scheduledCleanFlag", studyRule.isScheduledClean());
    }
}

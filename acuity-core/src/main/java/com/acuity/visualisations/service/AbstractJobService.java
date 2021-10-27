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
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.task.DurableJobDetailFactoryBean;
import com.acuity.visualisations.task.JobLauncherDetails;
import com.acuity.visualisations.util.ETLStudyRule;
import com.acuity.visualisations.util.ReadConfigurationTaskletConsts;
import lombok.AccessLevel;
import lombok.Getter;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.acuity.visualisations.util.JobLauncherConsts.AML_ENABLED;
import static com.acuity.visualisations.util.JobLauncherConsts.CONF_SOURCE_TYPE;
import static com.acuity.visualisations.util.JobLauncherConsts.JOB_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.PROJECT_KEY;
import static com.acuity.visualisations.util.JobLauncherConsts.STUDY_KEY;
import static java.util.stream.Collectors.toMap;

public abstract class AbstractJobService implements JobService, ApplicationContextAware {

    @Getter(AccessLevel.PACKAGE)
    private ApplicationContext applicationContext;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void refreshJobList(Scheduler scheduler) {

        Map<String, DurableJobDetailFactoryBean> jobListManual = getStudyRules().stream()
                .map(this::getDurableJobDetailFactoryBean)
                .collect(toMap(DurableJobDetailFactoryBean::getName, Function.identity()));

        try {
            for (JobKey key : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(getGroupName()))) {
                if (!jobListManual.containsKey(key.getName())) {
                    scheduler.deleteJob(key);
                }
            }

            for (Map.Entry<String, DurableJobDetailFactoryBean> detail : jobListManual.entrySet()) {
                scheduler.addJob(detail.getValue().getObject(), true);
            }
        } catch (SchedulerException e) {
            throw new MethodFailureException(e);
        }
    }

    @Override
    public void refreshJobList() {
        Scheduler scheduler = applicationContext.getBean("schedulerFactory", Scheduler.class);
        refreshJobList(scheduler);
    }

    DurableJobDetailFactoryBean getDurableJobDetailFactoryBean(ETLStudyRule studyRule) {
        DurableJobDetailFactoryBean item = new DurableJobDetailFactoryBean();
        item.setJobClass(JobLauncherDetails.class);
        item.setGroup(getGroupName());
        item.setDurability(true);
        Map<String, Object> jobDataAsMap = new HashMap<>();
        jobDataAsMap.put(JOB_NAME, getJobInstanceName());
        jobDataAsMap.put(STUDY_KEY, studyRule.getStudyCode());
        jobDataAsMap.put(PROJECT_KEY, studyRule.getDrugName());
        jobDataAsMap.put(AML_ENABLED, studyRule.isAmlEnabled());
        jobDataAsMap.put(CONF_SOURCE_TYPE, ReadConfigurationTaskletConsts.DB_CONF_SOURCE_TYPE); // we don't support XML config any more
        populateJobDetailFactoryBeanWithProperties(studyRule, jobDataAsMap);
        item.setJobDataAsMap(jobDataAsMap);
        String name = getJobName(studyRule.getStudyCode());
        item.setBeanName(name);
        item.setName(name);
        item.afterPropertiesSet();
        return item;
    }

    @Override
    public void runJobNow(String studyCode) {
        Scheduler scheduler = getApplicationContext().getBean("schedulerFactory", Scheduler.class);
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(new JobKey(getJobName(studyCode), getGroupName())).startNow().build();
        try {
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            throw new MethodFailureException(e);
        }
    }

    List<ETLStudyRule> getStudyRules() {
        return studyRuleDao.getEtlConfigList();
    }

    String getJobName(String studyCode) {
        return "runAcuityJob_" + studyCode;
    }

    abstract void populateJobDetailFactoryBeanWithProperties(ETLStudyRule studyRule, Map<String, Object> jobDataAsMap);

    abstract String getGroupName();

    abstract String getJobInstanceName();
}

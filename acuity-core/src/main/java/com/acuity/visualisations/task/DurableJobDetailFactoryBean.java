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

package com.acuity.visualisations.task;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

public class DurableJobDetailFactoryBean extends JobDetailFactoryBean {

    private String name;
    private String beanName;
    private String group;
    private String applicationContextJobDataKey;
    private ApplicationContext applicationContext;
    private Class<?> jobClass;
    private JobDetail jobDetail;
    private boolean durability;

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.name == null) {
            this.name = this.beanName;
        }
        if (this.group == null) {
            this.group = Scheduler.DEFAULT_GROUP;
        }
        if (this.applicationContextJobDataKey != null) {
            if (this.applicationContext == null) {
                throw new IllegalStateException("JobDetailBean needs to be set up in an ApplicationContext "
                        + "to be able to handle an 'applicationContextJobDataKey'");
            }
            getJobDataMap().put(this.applicationContextJobDataKey, this.applicationContext);
        }

        Class<?> jobDetailClass;
        try {
            jobDetailClass = getClass().getClassLoader().loadClass("org.quartz.impl.JobDetailImpl");
        } catch (ClassNotFoundException ex) {
            jobDetailClass = JobDetail.class;
        }
        BeanWrapper bw = new BeanWrapperImpl(jobDetailClass);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", this.name);
        pvs.add("group", this.group);
        pvs.add("jobClass", this.jobClass);
        pvs.add("jobDataMap", super.getJobDataMap());
        pvs.add("durability", durability);
        bw.setPropertyValues(pvs);
        this.jobDetail = (JobDetail) bw.getWrappedInstance();
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setApplicationContextJobDataKey(String applicationContextJobDataKey) {
        this.applicationContextJobDataKey = applicationContextJobDataKey;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setJobClass(@SuppressWarnings("rawtypes") Class jobClass) {
        this.jobClass = jobClass;
    }

    @Override
    public JobDetail getObject() {
        return this.jobDetail;
    }

    @Override
    public void setDurability(boolean durability) {
        this.durability = durability;
    }

}

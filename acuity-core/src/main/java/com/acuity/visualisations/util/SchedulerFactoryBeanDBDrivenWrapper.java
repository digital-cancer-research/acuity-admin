package com.acuity.visualisations.util;

import com.acuity.visualisations.service.JobService;
import org.quartz.Scheduler;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulerFactoryBeanDBDrivenWrapper implements FactoryBean<Scheduler>, BeanNameAware,
        InitializingBean, DisposableBean, SmartLifecycle {
    private Resource quartzConfigLocation;
    private int startupDelay;

    @Autowired
    private List<JobService> jobServices;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Required
    public void setQuartzConfigLocation(Resource quartzConfigLocation) {
        this.quartzConfigLocation = quartzConfigLocation;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }

    private SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

    public void initScheduler() {
        Map<String, Object> schedulerContextAsMap = new HashMap<String, Object>();
        schedulerContextAsMap.put("jobLocator", jobRegistry);
        schedulerContextAsMap.put("jobLauncher", jobLauncher);
        schedulerContextAsMap.put("jobExplorer", jobExplorer);
        schedulerFactoryBean.setSchedulerContextAsMap(schedulerContextAsMap);
        schedulerFactoryBean.setStartupDelay(startupDelay);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setConfigLocation(quartzConfigLocation);
    }


    @Override
    public Scheduler getObject() {
        Scheduler scheduler = schedulerFactoryBean.getObject();
        jobServices.forEach(s -> s.refreshJobList(scheduler));
        return scheduler;
    }

    @Override
    public Class<?> getObjectType() {
        return Scheduler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanName(String name) {
        schedulerFactoryBean.setBeanName(name);
    }

    @Override
    public void destroy() throws Exception {
        schedulerFactoryBean.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initScheduler();
        schedulerFactoryBean.afterPropertiesSet();
    }

    @Override
    public boolean isAutoStartup() {
        return schedulerFactoryBean.isAutoStartup();
    }

    @Override
    public void stop(Runnable callback) {
        schedulerFactoryBean.stop(callback);
    }

    @Override
    public void start() {
        schedulerFactoryBean.start();
    }

    @Override
    public void stop() {
        schedulerFactoryBean.stop();
    }

    @Override
    public boolean isRunning() {
        return schedulerFactoryBean.isRunning();
    }

    @Override
    public int getPhase() {
        return schedulerFactoryBean.getPhase();
    }
}

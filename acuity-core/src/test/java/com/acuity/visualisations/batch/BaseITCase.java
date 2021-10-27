package com.acuity.visualisations.batch;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.acuity.visualisations.batch.util.SchedulerSingletonStorage;
import com.acuity.visualisations.web.entity.JobExecutionEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/applicationContext.xml", "file:src/test/resources/applicationContext.xml" })
public abstract class BaseITCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseITCase.class);

	@Autowired
	@Qualifier("schedulerFactory")

	private Scheduler scheduler;

	@Autowired
	private SchedulerSingletonStorage storage;

	protected void launchTestJob(JobDetailFactoryBean jobDetailFactoryBean) {
		JobDetail jobDetail = jobDetailFactoryBean.getObject();
		try {
			if (scheduler.getJobDetail(jobDetail.getKey()) == null) {
				scheduler.addJob(jobDetail, false);
			}
			Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail.getKey()).startNow().build();
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Exception while starting test job.", e);
			Assert.fail();
		}

		try {
			JobExecutionEvent event = null;
			while (event == null || !event.isFinished()) {
				event = storage.getEvent();
				storage.saveState(event.getExecutionId());
				// LOGGER.info("Test job state: " + event.getExecutionState().toString());
			}
		} catch (Exception e) {
			LOGGER.error("Exception while processing test job.", e);
			Assert.fail();
		}

	}

}

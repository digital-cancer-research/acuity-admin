package com.acuity.visualisations.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import com.acuity.visualisations.batch.test.EventTypeTest;
import com.acuity.visualisations.batch.test.FilterByStudyCodeTest;
import com.acuity.visualisations.batch.test.LabGroupTest;
import com.acuity.visualisations.batch.test.TestPattern;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.service.UIFunctionsManager;

public class CommonTestITCase extends BaseITCase {

	@Autowired
	@Qualifier("runAcuityJob_Test")
	private JobDetailFactoryBean testJobFactoryBean;

	@Autowired
	private UIFunctionsManager functionsManager;

	@Autowired
	private LabGroupTest labGroupTest;

	@Autowired
	private EventTypeTest eventTypeTest;

	@Autowired
	private FilterByStudyCodeTest byStudyCodeTest;

	@Before
	public void cleanDB() {
		String projectName = testJobFactoryBean.getJobDataMap().getString(JobLauncherConsts.PROJECT_KEY);
		String studyName = testJobFactoryBean.getJobDataMap().getString(JobLauncherConsts.STUDY_KEY);
		Map<String, List<String>> studies = new HashMap<String, List<String>>();
		studies.put(projectName, Arrays.asList(new String[] { studyName }));
		functionsManager.deleteStudies(studies);
	}

	@Ignore
	@Test
	public void commonTest() {
		List<TestPattern> tests = new ArrayList<TestPattern>();
		tests.add(labGroupTest);
		tests.add(eventTypeTest);
		for (int i = 1; i <= 4; i++) {
			// given
			for (TestPattern test : tests) {
				test.setSource(i);
			}
			// when
			launchTestJob(testJobFactoryBean);
			// then
			for (TestPattern test : tests) {
				test.checkResult(i);
			}
		}
	}

	@Ignore
	@Test
	public void studyCodeTest() {
		for (int i = 1; i <= 3; i++) {
			// given
			byStudyCodeTest.setSource(i);
			// when
			launchTestJob(testJobFactoryBean);
			// then
			byStudyCodeTest.checkResult(i);
		}
	}
}

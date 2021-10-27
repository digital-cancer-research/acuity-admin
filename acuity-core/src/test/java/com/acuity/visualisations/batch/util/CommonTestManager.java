package com.acuity.visualisations.batch.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CommonTestManager {

	@Autowired
	private CommonTestDao commonTestDao;

	@Transactional
	public void clearExecutionHistory() {
		commonTestDao.clearJobExecutionHistory();
	}
}

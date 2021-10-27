package com.acuity.visualisations.batch.util;

import org.springframework.stereotype.Repository;

@Repository
public class CommonTestDao extends ACUITYTestDaoSupport {

	public void clearJobExecutionHistory() {
		getJdbcTemplate().update("delete from batch_step_execution_context");
		getJdbcTemplate().update("delete from batch_step_execution");
		getJdbcTemplate().update("delete from batch_job_execution_context");
		getJdbcTemplate().update("delete from batch_job_execution");
		getJdbcTemplate().update("delete from batch_job_execution_params");
		getJdbcTemplate().update("delete from batch_job_instance");
	}
}

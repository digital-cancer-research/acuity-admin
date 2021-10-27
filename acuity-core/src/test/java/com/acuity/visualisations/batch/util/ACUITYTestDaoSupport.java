package com.acuity.visualisations.batch.util;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public abstract class ACUITYTestDaoSupport extends JdbcDaoSupport {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@PostConstruct
	public final void init() {
		setDataSource(dataSource);
	}
}

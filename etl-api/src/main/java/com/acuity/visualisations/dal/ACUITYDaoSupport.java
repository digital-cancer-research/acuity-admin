package com.acuity.visualisations.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public abstract class ACUITYDaoSupport extends JdbcDaoSupport {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Value("${spring.datasource.fetch.size:256}")
    private int fetchSize;

    @PostConstruct
    public final void init() {
        setDataSource(dataSource);
        getJdbcTemplate().setFetchSize(fetchSize);
    }
}

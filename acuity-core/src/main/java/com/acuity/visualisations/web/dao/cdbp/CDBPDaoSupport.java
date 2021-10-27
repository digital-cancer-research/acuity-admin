package com.acuity.visualisations.web.dao.cdbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public class CDBPDaoSupport extends JdbcDaoSupport {
    @Autowired
    @Qualifier("CDBPdataSource")
    private DataSource dataSource;

    @PostConstruct
    public final void init() {
        setDataSource(dataSource);
    }
}

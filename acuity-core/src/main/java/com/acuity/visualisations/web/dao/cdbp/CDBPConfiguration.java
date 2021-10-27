package com.acuity.visualisations.web.dao.cdbp;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("cdbp")
@Configuration
public class CDBPConfiguration {

    @Value("jdbc.cdbp.driver")
    private String driver;

    @Value("jdbc.cdbp.url")
    private String url;

    @Value("jdbc.cdbp.user")
    private String username;

    @Value("jdbc.cdbp.password")
    private String password;

    @Bean("CDBPdataSource")
    public DataSource cdbpDataSource() {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

}

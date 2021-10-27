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

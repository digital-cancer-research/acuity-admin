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

package com.acuity.visualisations.mapping.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.context.request.async.DeferredResult;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class QuerySearchWorker<T> implements Runnable {

    private List<T> acuityResults;
    private List<T> cdbpResults;
    private String sqlACUITYQuery;
    private String sqlCDBPQuery;
    private Object[] acuityQueryParams;
    private Object[] cdbpQueryParams;
    private JdbcTemplate acuityTemplate;
    private JdbcTemplate cdbpTemplate;
    private RowMapper<T> rowACUITYMapper;
    private RowMapper<T> rowCDBPMapper;
    private boolean isRunning;
    private PreparedStatement preparedACUITYStmt;
    private PreparedStatement preparedCDBPStmt;
    private final BlockingQueue<DeferredResult<Boolean>> resultQueue = new LinkedBlockingDeque<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySearchWorker.class);
    private boolean hasBeenCancelled;
    private long completedMillis;

    public QuerySearchWorker(List<T> acuityResults, List<T> cdbpResults) {
        this.acuityResults = acuityResults;
        this.cdbpResults = cdbpResults;
    }

    @Override
    public void run() {
        isRunning = true;
        List<T> acuityResults;
        List<T> cdbpResults;
        try {
            if (sqlACUITYQuery != null) {
                acuityResults = acuityTemplate.query(connection -> {
                    preparedACUITYStmt = connection.prepareStatement(sqlACUITYQuery);
                    int nParams = acuityQueryParams.length;
                    for (int i = 0; i < nParams; i++) {
                        Object obj = acuityQueryParams[i];
                        preparedACUITYStmt.setObject(i + 1, obj);
                    }
                    return preparedACUITYStmt;
                }, rowACUITYMapper);

                this.acuityResults.addAll(acuityResults);
            }

            if (sqlCDBPQuery != null) {
                cdbpResults = cdbpTemplate.query(connection -> {
                    preparedCDBPStmt = connection.prepareStatement(sqlCDBPQuery);
                    int nParams = cdbpQueryParams.length;
                    for (int i = 0; i < nParams; i++) {
                        Object obj = cdbpQueryParams[i];
                        preparedCDBPStmt.setObject(i + 1, obj);
                    }
                    return preparedCDBPStmt;
                }, rowCDBPMapper);

                this.cdbpResults.addAll(cdbpResults);
            }
        } catch (Exception e) {
            LOGGER.error("Exception when execute query!", e);
        }
        isRunning = false;
        completedMillis = System.currentTimeMillis();
        DeferredResult<Boolean> result;
        try {
            result = resultQueue.poll(30, TimeUnit.SECONDS);
            if (result != null) {
                result.setResult(hasBeenCancelled);
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public void cancelQuery() {
        hasBeenCancelled = true;
        if (preparedACUITYStmt != null) {
            try {
                if (!preparedACUITYStmt.isClosed()) {
                    preparedACUITYStmt.cancel();
                }

            } catch (Exception e) {
                LOGGER.error("Exception when trying cancel query!");
            }
        }
        if (preparedCDBPStmt != null) {
            try {
                preparedCDBPStmt.cancel();
            } catch (Exception e) {
                LOGGER.error("Exception when trying cancel query!");
            }
        }
        DeferredResult<Boolean> result = null;
        try {
            result = resultQueue.poll(30, TimeUnit.SECONDS);
            if (result != null) {
                result.setResult(hasBeenCancelled);
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public String getSqlACUITYQuery() {
        return sqlACUITYQuery;
    }

    public String getSqlCDBPQuery() {
        return sqlCDBPQuery;
    }

    public JdbcTemplate getAcuityTemplate() {
        return acuityTemplate;
    }

    public JdbcTemplate getCdbpTemplate() {
        return cdbpTemplate;
    }

    public RowMapper<T> getRowACUITYMapper() {
        return rowACUITYMapper;
    }

    public RowMapper<T> getRowCDBPMapper() {
        return rowCDBPMapper;
    }

    public void setSqlACUITYQuery(String sqlACUITYQuery) {
        this.sqlACUITYQuery = sqlACUITYQuery;
    }

    public void setSqlCDBPQuery(String sqlCDBPQuery) {
        this.sqlCDBPQuery = sqlCDBPQuery;
    }

    public void setAcuityTemplate(JdbcTemplate acuityTemplate) {
        this.acuityTemplate = acuityTemplate;
    }

    public void setCdbpTemplate(JdbcTemplate cdbpTemplate) {
        this.cdbpTemplate = cdbpTemplate;
    }

    public void setRowACUITYMapper(RowMapper<T> rowACUITYMapper) {
        this.rowACUITYMapper = rowACUITYMapper;
    }

    public void setRowCDBPMapper(RowMapper<T> rowCDBPMapper) {
        this.rowCDBPMapper = rowCDBPMapper;
    }

    public Object[] getAcuityQueryParams() {
        return acuityQueryParams;
    }

    public void setAcuityQueryParams(Object[] acuityQueryParams) {
        this.acuityQueryParams = acuityQueryParams;
    }

    public Object[] getCdbpQueryParams() {
        return cdbpQueryParams;
    }

    public void setCdbpQueryParams(Object[] cdbpQueryParams) {
        this.cdbpQueryParams = cdbpQueryParams;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void getStatus(DeferredResult<Boolean> result) {
        resultQueue.add(result);
    }

    public long getCompletedMillis() {
        return completedMillis;
    }

    public void setCompletedMillis(long completedMillis) {
        this.completedMillis = completedMillis;
    }
}

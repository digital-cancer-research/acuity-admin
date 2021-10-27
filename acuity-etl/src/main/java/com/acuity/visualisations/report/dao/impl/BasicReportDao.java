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

package com.acuity.visualisations.report.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IBasicReportDao;
import com.acuity.visualisations.report.entity.Report;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class BasicReportDao<T extends Report> extends ACUITYDaoSupport implements IBasicReportDao<T> {

    private static final int DATA_ROW_LIMIT = 1000;
        
    public void batchInsert(final List<T> list) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                prepareStatementToInsert(ps, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        };

        getJdbcTemplate().batchUpdate(getInsertStatement(), bpss);
    }

    public void delete(final List<Long> jobExecutionIds) {
        if (jobExecutionIds.isEmpty()) {
            return;
        }
        getJdbcTemplate().update(getDeleteStatement(jobExecutionIds.size()), ps -> {
            for (int i = 0; i < jobExecutionIds.size(); i++) {
                ps.setLong(i + 1, jobExecutionIds.get(i));
            }
        });
    }

    private String getDeleteStatement(int jeNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ");
        builder.append(getTableName());
        builder.append(" where ");
        builder.append(getExecutionIdColumn());
        builder.append(" in (");
        for (int i = 0; i < jeNumber; i++) {
            builder.append("?, ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(")");
        return builder.toString();
    }

    protected String getSelectStatement() {
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT FROM ");
        builder.append(getTableName());
        builder.append(" WHERE ");
        builder.append(getStudyIdColumn());
        builder.append(" = ?");

        return builder.toString();
    }

    /**
     * Uses pagination to get rows from the lastPrimaryKey retrieved.
     * 
     * @returns The SQL string to query the database for report data.
     */
    protected String getSelectStatementForJobExecutionId() {
        StringBuilder builder = new StringBuilder();

        builder.append("SELECT * FROM (SELECT ");
        builder.append(getSelectFields());
        builder.append(" FROM ");
        builder.append(getTableName());
        builder.append(" WHERE ");
        builder.append(getExecutionIdColumn());
        builder.append(" = ? AND ");
        builder.append(getPrimaryKeyColumn());
        builder.append(" > ? ORDER BY " + getPrimaryKeyColumn());
        builder.append(") WHERE ROWNUM <= " + BasicReportDao.DATA_ROW_LIMIT);

        return builder.toString();
    }

    protected abstract String getSelectFields();
    
    protected abstract String getTableName();

    protected abstract String getStudyIdColumn();

    protected abstract List<? extends Report> selectReportData(long jobExecutionId, long nextPrimaryKey);

    protected abstract String getPrimaryKeyColumn();
    
    protected abstract String getExecutionIdColumn();

    protected abstract void prepareStatementToInsert(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getInsertStatement();

}

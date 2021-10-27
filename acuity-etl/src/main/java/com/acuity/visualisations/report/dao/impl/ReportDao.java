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

import com.acuity.visualisations.report.entity.Report;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ReportDao<T extends Report> extends BasicReportDao<T> {

    private static final int MAX_IN_CLAUSE_SIZE = 1000;

    public void batchUpdate(final List<T> list) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                prepareStatementToUpdate(ps, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        };

        getJdbcTemplate().batchUpdate(getUpdateStatement(), bpss);
    }

    public Map<String, Integer> findIds(final List<String> hashList, final String entityName) {
        if (hashList.isEmpty()) {
            return new HashMap<String, Integer>();
        }
        int from = 0;
        int to = MAX_IN_CLAUSE_SIZE > hashList.size() ? hashList.size() : MAX_IN_CLAUSE_SIZE;
        Map<String, Integer> partAnswer = new HashMap<String, Integer>();
        while (true) {
            List<String> partHashes = hashList.subList(from, to);
            partAnswer.putAll(findIdsByHashAndEntity(partHashes, entityName));
            if (to == hashList.size()) {
                break;
            }
            from = to + 1;
            to = from + MAX_IN_CLAUSE_SIZE < hashList.size() ? from + MAX_IN_CLAUSE_SIZE : hashList.size();
        }
        return partAnswer;

    }

    public List<T> findEntities(String studyCode, long executionId, String entityName, List<String> hashList) {
        if (hashList.isEmpty()) {
            return new ArrayList<T>();
        }
        int from = 0;
        int to = MAX_IN_CLAUSE_SIZE > hashList.size() ? hashList.size() : MAX_IN_CLAUSE_SIZE;
        List<T> partAnswer = new ArrayList<T>();
        while (true) {
            List<String> partHashes = hashList.subList(from, to);
            partAnswer.addAll(findEntitiesByHash(studyCode, executionId, entityName, partHashes));
            if (to == hashList.size()) {
                break;
            }
            from = to + 1;
            to = from + MAX_IN_CLAUSE_SIZE < hashList.size() ? from + MAX_IN_CLAUSE_SIZE : hashList.size();
        }
        return partAnswer;
    }

    private List<T> findEntitiesByHash(final String studyCode, final long executionId, final String entityName, final List<String> hashList) {

        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindEntity(hashList.size()));
            ps.setString(1, studyCode);
            ps.setLong(2, executionId);
            ps.setString(3, entityName);
            for (int i = 0; i < hashList.size(); i++) {
                ps.setString(i + 4, hashList.get(i));
            }
            return ps;
        }, getResultSetExtractor());
    }

    private HashMap<String, Integer> findIdsByHashAndEntity(final List<String> hashList, final String entityName) {
        final HashMap<String, Integer> toReturn = new HashMap<String, Integer>();

        getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindId(hashList.size()));
            ps.setString(1, entityName);
            for (int i = 0; i < hashList.size(); i++) {
                ps.setString(i + 2, hashList.get(i));
            }
            return ps;
        }, (ResultSet rs) -> {
            while (rs.next()) {
                String hash = rs.getString(getHashColumnName());
                Integer id = rs.getInt(getIdColumnName());
                toReturn.put(hash, id);
            }
            return toReturn;
        }
        );

        return toReturn;
    }

    protected String getSQLToFindEntity(int size) {
        StringBuilder sb = new StringBuilder();

        sb.append("select * from ");
        sb.append(getTableName());
        sb.append(" where ");
        sb.append(getStudyIdColumn());
        sb.append("= ?");
        sb.append(" and ");
        sb.append(getExecutionIdColumn());
        sb.append("= ?");
        sb.append(" and ");
        sb.append(getEntityNameColumn());
        sb.append("= ?");
        sb.append(" and ");
        sb.append(getHashColumnName());
        sb.append(" in ");
        sb.append("(");
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i != size - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    protected abstract void prepareStatementToUpdate(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getUpdateStatement();

    protected abstract ResultSetExtractor<List<T>> getResultSetExtractor();

    private String getSQLToFindId(int size) {
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        sb.append(getIdColumnName());
        sb.append(",");
        sb.append(getHashColumnName());
        sb.append(" from ");
        sb.append(getTableName());
        sb.append(" where ");
        sb.append(getEntityNameColumn());
        sb.append("= ?");
        sb.append(" and ");
        sb.append(getHashColumnName());
        sb.append(" in ");
        sb.append("(");
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i != size - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    protected abstract String getIdColumnName();

    protected abstract String getHashColumnName();

    protected abstract String getEntityNameColumn();

}

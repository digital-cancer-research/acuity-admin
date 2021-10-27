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

import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

public abstract class EntityDao<T extends OutputEntity> extends BasicEntityDao<T> implements IEntityDao<T> {

    public Map<OctetString, RowParameters> findHash(final String studyId, Class<?> entityClass) {
        return findHash(studyId, new HashMap<>(), entityClass);
    }

    public Map<OctetString, RowParameters> findHash(final String studyId, Map<OctetString, RowParameters> hashes, Class<?> entityClass) {
        JdbcTemplate select = getJdbcTemplate();
        select.query(con -> {
            String sql = getHashesStatement(entityClass);
            logger.debug(sql);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setFetchSize(1000);
            prepareHashesStatement(ps, studyId);
            return ps;
        }, (ResultSet rs) -> {
            while (rs.next()) {

                OctetString uniqueHash = new OctetString(rs.getString(EntityDao.this.getUniqueSha1ColumnName()));
                int secondaryHash = rs.getInt(EntityDao.this.getSecondaryHashColumnName());
                String id = rs.getString(EntityDao.this.getIdColumnName());
                RowParameters parameters = new RowParameters(secondaryHash, id, State.SYNCHRONIZED, false);
                hashes.put(uniqueHash, parameters);
            }
            return hashes;
        }
        );
        return hashes;
    }

    public abstract String getTableName();

    public HashMap<String, String> findIdsByHash(final List<String> hashList) {
        final HashMap<String, String> toReturn = new HashMap<>();

        getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindIdByHash(hashList.size()));
            for (int i = 0; i < hashList.size(); i++) {
                ps.setString(i + 1, hashList.get(i));
            }
            return ps;
        }, (ResultSet rs) -> {
            while (rs.next()) {
                String hash = rs.getString(getUniqueSha1ColumnName());
                String id = rs.getString(getIdColumnName());
                toReturn.put(hash, id);
            }
            return toReturn;
        }
        );

        return toReturn;
    }

    public HashMap<String, String> findIdsByRefHash(final List<String> hashList) {
        final HashMap<String, String> toReturn = new HashMap<String, String>();

        getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindIdByRefHash(hashList.size()));
            for (int i = 0; i < hashList.size(); i++) {
                ps.setString(i + 1, hashList.get(i));
            }
            return ps;
        }, (ResultSet rs) -> {
            while (rs.next()) {
                String hash = rs.getString(getUniqueSha1ColumnName());
                String id = rs.getString(getIdColumnName());
                toReturn.put(hash, id);
            }
            return toReturn;
        }
        );

        return toReturn;
    }

    public void updateState(final List<T> entities) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setTimestamp(1, getSQLTimestamp(entities.get(i).getDateUpdated()));
                ps.setString(2, entities.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return entities.size();
            }
        };

        getJdbcTemplate().batchUpdate(getUpdateDateStatement(), bpss);
    }

    public void deleteByIds(final List<String> ids) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        };

        getJdbcTemplate().batchUpdate(getDeleteByIdStatement(), bpss);
    }

    String getUpdateDateStatement() {
        return "update " + getTableName() + " set " + getTablePrefix() + "_DATE_UPDATED=? where " + getIdColumnName() + "=?";
    }

    String getDeleteByIdStatement() {
        return "delete from " + getTableName() + " where " + getIdColumnName() + "=?";
    }

    private String getSQLToFindIdByHash(int size) {
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        sb.append(getIdColumnName());
        sb.append(",");
        sb.append(getUniqueSha1ColumnName());
        sb.append(" from ");
        sb.append(getTableName());
        sb.append(" where ");
        sb.append(getUniqueSha1ColumnName());
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

    private String getSQLToFindIdByRefHash(int size) {
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        sb.append(getIdColumnName());
        sb.append(",");
        sb.append(getRefSha1ColumnName());
        sb.append(" from ");
        sb.append(getTableName());
        sb.append(" where ");
        sb.append(getRefSha1ColumnName());
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

    protected String getUniqueSha1ColumnName() {
        return getTablePrefix() + "_UNQ_SHA1";
    }

    protected String getRefSha1ColumnName() {
        return getTablePrefix() + "_REF_SHA1";
    }

    protected String getSecondaryHashColumnName() {
        return getTablePrefix() + "_SEC_HASH";
    }

    protected abstract String getTablePrefix();

    protected abstract String getHashesStatement(Class<?> entityClass);

    private void prepareHashesStatement(PreparedStatement ps, final String studyId) throws SQLException {
        ps.setString(1, studyId);
    }

}

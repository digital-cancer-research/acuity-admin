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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.mapping.dao.IExcludingValueDao;
import com.acuity.visualisations.mapping.entity.ExcludingValue;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by knml167 on 27/03/14.
 */
@Repository
public class ExcludingValueDao extends BasicDynamicEntityDao<ExcludingValue> implements IExcludingValueDao {
    //ExcludingValue
    private static final String MAP_EXCLUDING_VALUES_TABLE = "MAP_EXCLUDING_VALUES";
    private static final String ID_COLUMN = "MEV_ID";
    private static final RowMapper<ExcludingValue> ROW_MAPPER = (rs, rowNum) -> {
        ExcludingValue out = new ExcludingValue();
        out.setId(rs.getLong(ID_COLUMN));
        out.setStudyRuleId(rs.getLong("MEV_MSR_ID"));
        out.setFieldRuleId(rs.getLong("MEV_MFI_ID"));
        out.setValue(rs.getString("MEV_VALUE"));
        return out;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, ExcludingValue entity) throws SQLException {
        int paramIndex = 1;
        ps.setLong(paramIndex++, entity.getStudyRuleId());
        ps.setLong(paramIndex++, entity.getFieldRuleId());
        ps.setString(paramIndex, entity.getValue());
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = MAP_EXCLUDING_VALUES_TABLE;
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(ID_COLUMN).setValue("nextval('mev_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MEV_MSR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MEV_MFI_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MEV_VALUE").build());
        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, ExcludingValue entity) throws SQLException {
        int paramIndex = 1;
        ps.setLong(paramIndex++, entity.getStudyRuleId());
        ps.setLong(paramIndex++, entity.getFieldRuleId());
        ps.setString(paramIndex++, entity.getValue());
        ps.setLong(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = MAP_EXCLUDING_VALUES_TABLE;
        List<TableField> fieldsToInsert = new ArrayList<>();
        List<TableField> whereFields = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MEV_MSR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MEV_MFI_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MEV_VALUE").build());
        whereFields.add(fieldBuilder.setField(ID_COLUMN).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
    }

    @Override
    protected String getIdColumnName() {
        return ID_COLUMN;
    }

    public List<ExcludingValue> getExcludingValuesByStudyRule(long studyRuleId) {
        String sql = "select * from MAP_EXCLUDING_VALUES where MEV_MSR_ID=?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyRuleId);
    }

    public void deleteStudyExcludingValues(long studyRuleId) {
        String sql = "delete from MAP_EXCLUDING_VALUES where MEV_MSR_ID=?";
        getJdbcTemplate().update(sql, studyRuleId);
    }

    public void insertExcludingValues(final List<ExcludingValue> excludingValues) {
        String sql = "insert into MAP_EXCLUDING_VALUES "
                + "(MEV_ID, MEV_MSR_ID, MEV_MFI_ID, MEV_VALUE) "
                + "values (nextval('MEV_SEQ'),?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, excludingValues.get(i).getStudyRuleId());
                ps.setLong(2, excludingValues.get(i).getFieldRuleId());
                ps.setString(3, excludingValues.get(i).getValue());
            }

            @Override
            public int getBatchSize() {
                return excludingValues.size();
            }
        });
    }
}

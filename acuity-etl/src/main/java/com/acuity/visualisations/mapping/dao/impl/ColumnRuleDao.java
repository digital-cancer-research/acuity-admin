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
import com.acuity.visualisations.mapping.dao.IColumnRuleDao;
import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.MappingRule;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ColumnRuleDao extends BasicDynamicEntityDao<ColumnRule> implements IColumnRuleDao {
    private static final String MCR_ID = "MCR_ID";

    private static final RowMapper<ColumnRule> ROW_MAPPER = (rs, rowNum) -> {
        ColumnRule rule = new ColumnRule();
        rule.setId(rs.getLong(MCR_ID));
        rule.setName(rs.getString("MCR_NAME"));
        rule.setMappingRuleId(rs.getLong("MCR_MMR_ID"));
        return rule;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, ColumnRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getName());
        ps.setLong(paramIndex, entity.getMappingRule().getId());
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_COLUMN_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(MCR_ID).setValue("nextval('mcr_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MCR_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MCR_MMR_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getIdColumnName() {
        return MCR_ID;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, ColumnRule entity) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    protected String getUpdateStatement() {
        throw new NotImplementedException();
    }

    public void delete(MappingRule mappingRule) {
        getJdbcTemplate().update("delete from MAP_COLUMN_RULE where MCR_MMR_ID = ?", new Object[]{mappingRule.getId()});
    }

    public List<ColumnRule> getColumnRulesByStudy(long studyId) {
        String sql = "select MCR_ID, MCR_NAME, MCR_MMR_ID from MAP_COLUMN_RULE"
                + " join MAP_MAPPING_RULE on MCR_MMR_ID=MMR_ID"
                + " join MAP_FILE_RULE on MMR_MFR_ID=MFR_ID"
                + " where MFR_MSR_ID=? ORDER BY MCR_ID";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyId);
    }

    @Override
    public List<ColumnRule> getColumnRulesForFileRule(long fileRuleId) {
        String sql = "select MCR_ID, MCR_NAME, MCR_MMR_ID "
                + "from MAP_COLUMN_RULE inner join MAP_MAPPING_RULE on MCR_MMR_ID=MMR_ID "
                + "where MMR_MFR_ID=? ORDER BY MCR_ID";
        return getJdbcTemplate().query(sql, ROW_MAPPER, fileRuleId);
    }
}

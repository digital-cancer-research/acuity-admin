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
import com.acuity.visualisations.mapping.dao.IMappingRuleDao;
import com.acuity.visualisations.mapping.entity.MappingRule;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MappingRuleDao extends BasicDynamicEntityDao<MappingRule> implements IMappingRuleDao {
    private static final String MMR_ID = "MMR_ID";

    private static final RowMapper<MappingRule> ROW_MAPPER = (rs, rowNum) -> {
        MappingRule mappingRule = new MappingRule();
        mappingRule.setId(rs.getLong(MMR_ID));
        mappingRule.setFmtName(rs.getString("MMR_FMT_NAME"));
        mappingRule.setValue(rs.getString("MMR_VALUE"));
        mappingRule.setAggregationFunctionId(rs.getLong("MMR_MAF_ID"));
        mappingRule.setFileRuleId(rs.getLong("MMR_MFR_ID"));
        return mappingRule;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, MappingRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getFmtName());
        ps.setString(paramIndex++, entity.getValue());
        ps.setObject(paramIndex++, entity.getAggregationFunction().getId());
        ps.setLong(paramIndex++, entity.getFileRule().getId());
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_MAPPING_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(MMR_ID).setValue("nextval('mmr_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MMR_FMT_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MMR_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("MMR_MAF_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MMR_MFR_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getIdColumnName() {
        return MMR_ID;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, MappingRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getFmtName());
        ps.setString(paramIndex++, entity.getValue());
        ps.setLong(paramIndex++, entity.getAggregationFunction().getId());
        ps.setLong(paramIndex++, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = "MAP_MAPPING_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        List<TableField> whereFields = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MMR_FMT_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MMR_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("MMR_MAF_ID").build());
        whereFields.add(fieldBuilder.setField(MMR_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
    }

    public void delete(MappingRule mappingRule) {
        getJdbcTemplate().update("delete from MAP_MAPPING_RULE where MMR_ID=?", new Object[]{mappingRule.getId()});
    }

    public List<MappingRule> getMappingRulesByStudyRule(long studyRuleId) {
        String sql = "select MAP_MAPPING_RULE.* from MAP_MAPPING_RULE"
                + " inner join MAP_FILE_RULE on MMR_MFR_ID=MFR_ID"
                + " where MFR_MSR_ID=?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyRuleId);
    }

    public List<MappingRule> getMappingRulesForFileRule(long fileRuleId) {
        String sql = "select * from MAP_MAPPING_RULE where MMR_MFR_ID=?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, fileRuleId);
    }
}

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
import com.acuity.visualisations.mapping.dao.IProjectGroupRuleDaoBase;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProjectGroupRuleDaoBase extends BasicDynamicEntityDao<GroupRuleBase> implements IProjectGroupRuleDaoBase {
    private final String groupDeleteQuery = "delete from " + getTableName() + " where " + getPrefix() + "_ID=?";
    private final String getByProjectQuery = "select * from " + getTableName() + " where " + getPrefix() + "_PROJECT_ID = ?";

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, GroupRuleBase entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getTime());
        ps.setString(paramIndex++, entity.getDefaultValue());
        ps.setString(paramIndex++, entity.getName());
        ps.setString(paramIndex++, entity.getDataSource());
        ps.setObject(paramIndex++, entity.getReady() ? 1 : 0);
        ps.setLong(paramIndex, entity.getParentId());

    }

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_ID").setValue("nextval('" + getPrefix() + "_seq')").build());
        fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_TIME").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_DEFAULT_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_DATA_SOURCE").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_ENABLED").build());
        fieldsToInsert.add(fieldBuilder.setField(getParentColumnName()).build());
        String sql = QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
        return sql;
    }

    public List<GroupRuleBase> listGropingsByRelation(final Long relatedId) {
        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select * from " + getTableName() + " where " + getIdColumnName() + " in" + " (select "
                    + getGroupRelationId() + "  from " + getRelationTableName() + " where " + getRelationId() + " = ?)");
            ps.setLong(1, relatedId);
            return ps;
        }, (rs, rowNum) -> {
            GroupRuleBase result = createInstance();
            result.setId(rs.getLong(getPrefix() + "_ID"));
            result.setName(rs.getString(getPrefix() + "_NAME"));
            result.setDefaultValue(rs.getString(getPrefix() + "_DEFAULT_VALUE"));
            result.setTime(rs.getString(getPrefix() + "_TIME"));
            result.setDataSource(rs.getString(getPrefix() + "_DATA_SOURCE"));
            result.setReady((rs.getInt(getPrefix() + "_ENABLED") == 1) ? true : false);
            return result;
        }
        );
    }

    public void delete(GroupRuleBase group) {
        getJdbcTemplate().update(groupDeleteQuery, new Object[]{group.getId()});
    }

    public List<GroupRuleBase> getByProjectId(Long id) {
        return getJdbcTemplate().query(getByProjectQuery, new Object[]{id}, (rs, rowNum) -> {
            GroupRuleBase result = createInstance();
            result.setId(rs.getLong(getPrefix() + "_ID"));
            result.setName(rs.getString(getPrefix() + "_NAME"));
            result.setDefaultValue(rs.getString(getPrefix() + "_DEFAULT_VALUE"));
            result.setTime(rs.getString(getPrefix() + "_TIME"));
            result.setDataSource(rs.getString(getPrefix() + "_DATA_SOURCE"));
            result.setReady((rs.getInt(getPrefix() + "_ENABLED") == 1) ? true : false);
            return result;
        });
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, GroupRuleBase entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getTime());
        ps.setString(paramIndex++, entity.getDefaultValue());
        ps.setString(paramIndex++, entity.getName());
        ps.setString(paramIndex++, entity.getDataSource());
        ps.setObject(paramIndex++, entity.getReady() ? 1 : 0);
        ps.setLong(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        List<TableField> whereFields = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_TIME").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_DEFAULT_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_DATA_SOURCE").build());
        fieldsToInsert.add(fieldBuilder.setField(getPrefix() + "_ENABLED").build());
        whereFields.add(fieldBuilder.setField(getPrefix() + "_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
        return sql;
    }

    protected abstract String getTableName();

    protected abstract String getRelationTableName();

    protected abstract String getGroupRelationId();

    protected abstract String getRelationId();

    protected abstract String getPrefix();

    protected abstract String getParentColumnName();

    protected abstract GroupRuleBase createInstance();
}

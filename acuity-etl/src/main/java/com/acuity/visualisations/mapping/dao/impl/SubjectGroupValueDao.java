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
import com.acuity.visualisations.mapping.dao.ISubjectGroupValueDao;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.SubjectGroupValueRule;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class SubjectGroupValueDao extends BasicDynamicEntityDao<SubjectGroupValueRule> implements ISubjectGroupValueDao {
    private static final String GROUP_VALUE_DELETE_ALL_QUERY = "delete from MAP_SUBJECT_GROUP_VALUE_RULE where MSGV_GROUP_ID=?";
    private static final String GET_BY_GROUP_QUERY = "select * from MAP_SUBJECT_GROUP_VALUE_RULE where MSGV_GROUP_ID = ?";

    public void deleteAll(GroupRuleBase group) {
        getJdbcTemplate().update(GROUP_VALUE_DELETE_ALL_QUERY, new Object[]{group.getId()});
    }

    public Collection<SubjectGroupValueRule> getByGroupId(Long id) {
        return getJdbcTemplate().query(GET_BY_GROUP_QUERY, new Object[]{id}, (rs, rowNum) -> {
            SubjectGroupValueRule result = new SubjectGroupValueRule();
            result.setId(rs.getLong("MSGV_ID"));
            result.setName(rs.getString("MSGV_NAME") == null ? null : rs.getString("MSGV_NAME").trim());
            result.setSubjectId(rs.getString("MSGV_SUBJECT_ID") == null ? null : rs.getString("MSGV_SUBJECT_ID").trim());
            result.setGroupId(rs.getLong("MSGV_GROUP_ID"));
            return result;
        });
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, SubjectGroupValueRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getName());
        ps.setString(paramIndex++, entity.getSubjectId());
        ps.setLong(paramIndex, entity.getGroupId());
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_SUBJECT_GROUP_VALUE_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MSGV_ID").setValue("nextval('msgv_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MSGV_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MSGV_SUBJECT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSGV_GROUP_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getIdColumnName() {
        return "MSGV_ID";
    }

    @Override
    protected String getUpdateStatement() {
        throw new NotImplementedException();
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, SubjectGroupValueRule entity) throws SQLException {
        throw new NotImplementedException();
    }
}

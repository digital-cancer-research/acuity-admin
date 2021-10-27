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

package com.acuity.visualisations.dal.dao;

import com.acuity.visualisations.dal.BasicEntityDao;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.Project;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class ProjectDao extends BasicEntityDao<Project> {
    private static final String PRJ_ID = "PRJ_ID";

    @Override
    protected String getInsertStatement() {
        String tagetTable = "RESULT_PROJECT";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);

        fieldsToInsert.add(fieldBuilder.setField(PRJ_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("PRJ_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PRJ_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PRJ_NAME").build());

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = "RESULT_PROJECT";
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("PRJ_DATE_UPDATED").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(PRJ_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Project entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex, entity.getProjectName());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Project entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return PRJ_ID;
    }

    public String getProjectGuid(final String projectName) {
        List<String> list = getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select PRJ_ID from RESULT_PROJECT where PRJ_NAME=?");
            ps.setString(1, projectName);
            return ps;
        }, (rs, rowNum) -> rs.getString(PRJ_ID)
        );
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    protected String getTableName() {
        return "RESULT_PROJECT";
    }

}

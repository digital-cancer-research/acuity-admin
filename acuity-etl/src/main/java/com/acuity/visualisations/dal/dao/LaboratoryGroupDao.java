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

import com.acuity.visualisations.dal.util.JoinDeclaration;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.LaboratoryGroup;
import java.util.Collections;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class LaboratoryGroupDao extends NotCumulativeEntityDao<LaboratoryGroup> {

    private static final String DEFAULT_GROUP = "Other labs";
    private static final String RESULT_LAB_GROUP = "RESULT_LAB_GROUP";

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_LAB_GROUP;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("LGR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_LAB_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_LAB_DESCR").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("LGR_STD_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_LAB_GROUP;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("LGR_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("LGR_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("LGR_LAB_DESCR").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("LGR_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, LaboratoryGroup entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getGroupName());
        ps.setString(paramIndex++, entity.getLabCode());
        ps.setString(paramIndex++, entity.getDescription());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setString(paramIndex, entity.getStudyGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, LaboratoryGroup entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getGroupName());
        ps.setString(paramIndex++, entity.getDescription());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "LGR_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_LAB_GROUP;
    }

    @Override
    public String getTablePrefix() {
        return "LGR";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField(RESULT_LAB_GROUP, "LGR_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("LGR_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_LAB_GROUP, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    protected String getDefaultFK() {
        return DEFAULT_GROUP;
    }

    @Override
    protected String getUpdateFKProcedureName() {
        return "acuity_utils.update_fk_to_lgr";
    }

    @Override
    protected String getStudyIdColumnName() {
        return "LGR_STD_ID";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

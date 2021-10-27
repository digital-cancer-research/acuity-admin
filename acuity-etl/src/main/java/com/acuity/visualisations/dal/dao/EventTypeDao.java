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

import com.acuity.visualisations.dal.EntityDao;
import com.acuity.visualisations.dal.util.JoinDeclaration;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.EventType;
import java.util.Collections;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class EventTypeDao extends EntityDao<EventType> {
    private static final String RESULT_EVENT_TYPE = "RESULT_EVENT_TYPE";
    private static final String EVT_ID = "EVT_ID";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_EVENT_TYPE;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(EVT_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_HLT").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_LLT").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_PT").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_SOC").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_MEDDRA_VERSION").build());
        fieldsToInsert.add(fieldBuilder.setField("EVT_STD_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_EVENT_TYPE;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("EVT_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("EVT_LLT").build());
        fieldsToSet.add(fieldBuilder.setField("EVT_MEDDRA_VERSION").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(EVT_ID).build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, EventType entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getHLT());
        ps.setString(paramIndex++, entity.getLLT());
        ps.setString(paramIndex++, entity.getPT());
        ps.setString(paramIndex++, entity.getSOC());
        ps.setBigDecimal(paramIndex++, entity.getMedDRAVersion());
        ps.setString(paramIndex, entity.getStudyGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, EventType entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getLLT());
        ps.setBigDecimal(paramIndex++, entity.getMedDRAVersion());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return EVT_ID;
    }

    @Override
    public String getTableName() {
        return RESULT_EVENT_TYPE;
    }

    @Override
    public String getTablePrefix() {
        return "EVT";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField(RESULT_EVENT_TYPE, "EVT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("EVT_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_EVENT_TYPE, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

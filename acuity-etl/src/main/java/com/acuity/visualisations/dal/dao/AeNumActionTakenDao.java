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
import com.acuity.visualisations.dal.util.JoinDeclarationBuilder;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.AeNumActionTaken;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class AeNumActionTakenDao extends EntityDao<AeNumActionTaken> {
    private static final String AENAT_ID = "AENAT_ID";
    private static final String RESULT_AE_NUM_ACT_TAKEN = "RESULT_AE_NUM_ACT_TAKEN";

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(AENAT_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("AENAT_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("AENAT_MDS_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("AENAT_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("AENAT_NUM_ACT_TAKEN").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("AENAT_NUM_ACT_TAKEN").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(AENAT_ID).build());
        String sql = QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, AeNumActionTaken entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());

        ps.setString(paramIndex++, entity.getDoseGuid());
        ps.setObject(paramIndex++, entity.getPatientGuid());
        ps.setObject(paramIndex, entity.getNumActionTaken());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, AeNumActionTaken entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setObject(paramIndex++, entity.getId());
        ps.setObject(paramIndex, entity.getNumActionTaken());
    }

    @Override
    protected String getIdColumnName() {
        return AENAT_ID;
    }

    @Override
    public String getTableName() {
        return RESULT_AE_NUM_ACT_TAKEN;
    }

    @Override
    protected String getTablePrefix() {
        return "AENAT";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain
                .add(builder.setSourceEntity(RESULT_AE_NUM_ACT_TAKEN).setTargetEntity("RESULT_PATIENT").addColumnToJoin("AENAT_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("AENAT_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_AE_NUM_ACT_TAKEN, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

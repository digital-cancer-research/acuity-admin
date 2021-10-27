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
import com.acuity.visualisations.model.output.entities.Drug;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class DrugDao extends EntityDao<Drug> {
    private static final String RESULT_DRUG = "RESULT_DRUG";
    private static final String DRUG_ID = "DRUG_ID";

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(DRUG_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("DRUG_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("DRUG_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DRUG_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DRUG_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("DRUG_STD_ID").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("DRUG_DATE_UPDATED").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(DRUG_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Drug entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getDrugName());
        ps.setString(paramIndex, entity.getStudyGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Drug entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return DRUG_ID;
    }

    @Override
    public String getTableName() {
        return RESULT_DRUG;
    }

    @Override
    public String getTablePrefix() {
        return "DRUG";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField(RESULT_DRUG, "DRUG_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add("DRUG_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_DRUG, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

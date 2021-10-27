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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.acuity.visualisations.dal.EntityDao;
import com.acuity.visualisations.dal.util.JoinDeclaration;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.Medicine;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class MedicineDao extends EntityDao<Medicine> {
    private static final String RESULT_MEDICINE = "RESULT_MEDICINE";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_MEDICINE;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MED_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_DRUG_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_DRUG_PARENT").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("MED_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("MED_STD_ID").build());
        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_MEDICINE;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField("MED_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("MED_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Medicine entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getDrugName());
        ps.setString(paramIndex++, entity.getDrugParent());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setString(paramIndex, entity.getStudyGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Medicine entity) throws SQLException {
        int parameter = 1;
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "MED_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_MEDICINE;
    }

    @Override
    public String getTablePrefix() {
        return "MED";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField(RESULT_MEDICINE, "MED_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("MED_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_MEDICINE, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

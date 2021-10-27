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
import com.acuity.visualisations.model.output.entities.RecistNonTargetLesion;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class RecistNonTargetLesionDao extends EntityDao<RecistNonTargetLesion> {

    @Override
    protected String getInsertStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("RNTL_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_REF_SHA1").build());

        fieldsToInsert.add(fieldBuilder.setField("RNTL_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("RNTL_VISIT_NUMBER").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_VISIT_DATE").build());

        fieldsToInsert.add(fieldBuilder.setField("RNTL_LESION_PRESENT").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_LESION_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_LESION_SITE").build());
        fieldsToInsert.add(fieldBuilder.setField("RNTL_RESPONSE").build());

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, RecistNonTargetLesion entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());

        ps.setObject(paramIndex++, entity.getPatientGuid());

        ps.setBigDecimal(paramIndex++, entity.getVisitNumber());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));

        ps.setString(paramIndex++, entity.getLesionPresent());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getLesionDate()));
        ps.setString(paramIndex++, entity.getLesionSite());
        ps.setString(paramIndex, entity.getResponse());
    }

    @Override
    protected String getIdColumnName() {
        return "RNTL_ID";
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, RecistNonTargetLesion entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getLesionPresent());
        ps.setObject(paramIndex++, entity.getVisitDate());
        ps.setObject(paramIndex++, entity.getVisitNumber());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("RNTL_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("RNTL_LESION_PRESENT").build());
        fieldsToSet.add(fieldBuilder.setField("RNTL_VISIT_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("RNTL_VISIT_NUMBER").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("RNTL_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    public String getTableName() {
        return "RESULT_RECIST_NONTARGET_LESION";
    }

    @Override
    protected String getTablePrefix() {
        return "RNTL";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder
                .setSourceEntity("RESULT_RECIST_NONTARGET_LESION")
                .setTargetEntity("RESULT_PATIENT")
                .addColumnToJoin("RNTL_PAT_ID", "PAT_ID").build());

        List<TableField> fieldValues = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("RNTL_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery("RESULT_RECIST_NONTARGET_LESION", hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT RNTL_PAT_ID FROM RESULT_RECIST_NONTARGET_LESION "
                + "left join RESULT_PATIENT on (RNTL_PAT_ID = PAT_ID) "
                + "left join RESULT_STUDY on (PAT_STD_ID = STD_ID) "
                + "where STD_NAME = ?", String.class, studyName);
    }
}

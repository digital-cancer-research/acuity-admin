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
import com.acuity.visualisations.model.output.entities.Radiotherapy;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

/**
 * Created by knml167 on 21/01/14.
 */
@Repository
public class RadiotherapyDao extends EntityDao<Radiotherapy> {
    private static final String RESULT_RADIOTHERAPY = "RESULT_RADIOTHERAPY";

    @Override
    public String getTableName() {
        return RESULT_RADIOTHERAPY;
    }

    @Override
    public String getTablePrefix() {
        return "RAD";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_RADIOTHERAPY).setTargetEntity("RESULT_PATIENT").addColumnToJoin("RAD_PAT_ID", "PAT_ID")
                .build());

        List<TableField> fieldValues = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add(getTablePrefix() + "_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_RADIOTHERAPY, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT RAD_PAT_ID FROM RESULT_RADIOTHERAPY rt "
                + "left join RESULT_PATIENT p on (rt.RAD_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_RADIOTHERAPY;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("RAD_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_VISIT_DAT").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_GIVEN").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_SITE_OR_REGION").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_START_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_END_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_TREATMENT_STATUS").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_RADIATION_DOSE").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_NUMBER_OF_DOSES").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RAD_TIME_STATUS").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Radiotherapy entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setString(paramIndex++, entity.getRadiotherapyGiven());
        ps.setString(paramIndex++, entity.getRadioSiteOrRegion());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRadioStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRadioEndDate()));
        ps.setString(paramIndex++, entity.getTreatmentStatus());
        ps.setBigDecimal(paramIndex++, entity.getRadiationDose());
        ps.setObject(paramIndex++, entity.getNumberOfDoses());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex++, entity.getPatientGuid());
        ps.setObject(paramIndex, entity.getRadioTimeStatus());
    }

    @Override
    protected String getIdColumnName() {
        return getTablePrefix() + "_ID";
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Radiotherapy entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setString(paramIndex++, entity.getRadiotherapyGiven());
        ps.setString(paramIndex++, entity.getRadioSiteOrRegion());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRadioStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRadioEndDate()));
        ps.setString(paramIndex++, entity.getTreatmentStatus());
        ps.setBigDecimal(paramIndex++, entity.getRadiationDose());
        ps.setObject(paramIndex++, entity.getNumberOfDoses());
        ps.setObject(paramIndex++, entity.getRadioTimeStatus());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_RADIOTHERAPY;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("RAD_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_VISIT").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_GIVEN").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_SITE_OR_REGION").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_END_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_START_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_TREATMENT_STATUS").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_RADIATION_DOSE").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_NUMBER_OF_DOSES").build());
        fieldsToSet.add(fieldBuilder.setField("RAD_TIME_STATUS").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(getTablePrefix() + "_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }
}

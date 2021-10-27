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
import com.acuity.visualisations.model.output.entities.PatientData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PatientDataDao extends EntityDao<PatientData> {
    private static final String RESULT_PATIENT_REPORTED_DATA = "RESULT_PATIENT_REPORTED_DATA";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_PATIENT_REPORTED_DATA;
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("RD_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_MEASUREMENT_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_MEASUREMENT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_REPORT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_COMMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_SRC_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_SRC_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RD_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("RD_REF_SHA1").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_PATIENT_REPORTED_DATA;
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("RD_MEASUREMENT_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("RD_VALUE").build());
        fieldsToSet.add(fieldBuilder.setField("RD_MEASUREMENT_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("RD_REPORT_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("RD_COMMENT").build());
        fieldsToSet.add(fieldBuilder.setField("RD_SRC_TYPE").build());
        fieldsToSet.add(fieldBuilder.setField("RD_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("RD_DATE_UPDATED").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("RD_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, PatientData entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getMeasurementName());
        ps.setBigDecimal(paramIndex++, entity.getValue());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getMeasurementDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getReportDate()));
        ps.setString(paramIndex++, entity.getComment());
        ps.setObject(paramIndex++, entity.getSourceType());
        ps.setString(paramIndex++, entity.getUnit());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex++, entity.getSourceGiud());
        ps.setObject(paramIndex++, entity.getPatientGuid());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex, entity.getFirstSha1ForReferencedFields());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, PatientData entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getMeasurementName());
        ps.setBigDecimal(paramIndex++, entity.getValue());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getMeasurementDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getReportDate()));
        ps.setString(paramIndex++, entity.getComment());
        ps.setObject(paramIndex++, entity.getSourceType());
        ps.setString(paramIndex++, entity.getUnit());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "RD_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_PATIENT_REPORTED_DATA;
    }

    @Override
    protected String getTablePrefix() {
        return "RD";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_PATIENT_REPORTED_DATA).setTargetEntity("RESULT_PATIENT").addColumnToJoin("RD_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add("RD_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_PATIENT_REPORTED_DATA, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT PAT_ID FROM RESULT_PATIENT_REPORTED_DATA rd "
                + "left join RESULT_PATIENT p on (rd.RD_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

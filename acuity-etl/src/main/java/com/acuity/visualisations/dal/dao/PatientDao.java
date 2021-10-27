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
import com.acuity.visualisations.model.output.entities.Patient;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PatientDao extends EntityDao<Patient> {
    private static final String RESULT_PATIENT = "RESULT_PATIENT";

    @Override
    protected String getIdColumnName() {
        return "PAT_ID";
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Patient patient) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, patient.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getBirthDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getVisitDate()));
        ps.setObject(paramIndex++, patient.getCentre());
        ps.setObject(paramIndex++, patient.getRace());
        ps.setObject(paramIndex++, patient.getSex());
        ps.setString(paramIndex++, patient.getSubject());
        ps.setString(paramIndex++, patient.getPart());
        ps.setString(paramIndex++, patient.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, patient.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, patient.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex++, patient.getPatientGroupGuid());
        ps.setObject(paramIndex, patient.getStudyGuid());
    }

    protected void prepareStatementToUpdate(PreparedStatement ps, Patient patient) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getBirthDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patient.getVisitDate()));
        ps.setObject(paramIndex++, patient.getCentre());
        ps.setObject(paramIndex++, patient.getRace());
        ps.setObject(paramIndex++, patient.getSex());
        ps.setObject(paramIndex++, patient.getPart());
        ps.setObject(paramIndex++, patient.getIntHashForSecondaryFields());
        ps.setObject(paramIndex, patient.getId());
    }

    protected String getInsertStatement() {
        String targetTable = RESULT_PATIENT;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_BIRTHDAT").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_VISDAT").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_CENTRE").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_RACE").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_SEX").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_SUBJECT").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_PART").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_PGR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PAT_STD_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    protected String getUpdateStatement() {
        String tagetTable = RESULT_PATIENT;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("PAT_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_BIRTHDAT").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_VISDAT").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_CENTRE").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_RACE").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_SEX").build());
        fieldsToSet.add(fieldBuilder.setField("PAT_PART").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("PAT_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    public String getTableName() {
        return RESULT_PATIENT;
    }

    @Override
    public String getTablePrefix() {
        return "PAT";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField(RESULT_PATIENT, "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("PAT_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_PATIENT, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT PAT_ID FROM RESULT_PATIENT p "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

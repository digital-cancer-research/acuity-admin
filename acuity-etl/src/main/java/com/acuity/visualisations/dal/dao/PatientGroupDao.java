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
import com.acuity.visualisations.model.output.entities.PatientGroup;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PatientGroupDao extends EntityDao<PatientGroup> {
    private static final String RESULT_PATIENT_GROUP = "RESULT_PATIENT_GROUP";

    @Override
    protected String getIdColumnName() {
        return "PGR_ID";
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, PatientGroup entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSubject());
        ps.setString(paramIndex++, entity.getPart());
        ps.setString(paramIndex++, entity.getGroupName());
        ps.setString(paramIndex++, entity.getGroupingName());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setObject(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex, entity.getStudyGuid());
    }

    protected String getInsertStatement() {
        String targetTable = RESULT_PATIENT_GROUP;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("PGR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_PAT_SUBJECT").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_PAT_PART").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_GROUP_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_GROUPING_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("PGR_STD_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    protected void prepareStatementToUpdate(PreparedStatement ps, PatientGroup patientGroup) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(patientGroup.getDateUpdated()));
        ps.setString(paramIndex++, patientGroup.getPart());
        ps.setString(paramIndex++, patientGroup.getGroupName());
        ps.setString(paramIndex++, patientGroup.getGroupingName());
        ps.setObject(paramIndex++, patientGroup.getIntHashForSecondaryFields());
        ps.setObject(paramIndex, patientGroup.getId());
    }

    protected String getUpdateStatement() {
        String tagetTable = RESULT_PATIENT_GROUP;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("PGR_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("PGR_PAT_PART").build());
        fieldsToSet.add(fieldBuilder.setField("PGR_GROUP_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("PGR_GROUPING_NAME").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("PGR_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    public String getTableName() {
        return RESULT_PATIENT_GROUP;
    }

    @Override
    public String getTablePrefix() {
        return "PGR";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField(RESULT_PATIENT_GROUP, "PGR_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("PGR_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_PATIENT_GROUP, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select DISTINCT PGR_PAT_SUBJECT from "
                + "RESULT_PATIENT_GROUP g "
                + "LEFT JOIN RESULT_STUDY s ON (g.PGR_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

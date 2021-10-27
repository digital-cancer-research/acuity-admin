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
import com.acuity.visualisations.model.output.entities.RecistAssessment;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class RecistAssessmentDao extends EntityDao<RecistAssessment> {
    private static final String RESULT_RECIST_ASSESSMENT = "RESULT_RECIST_ASSESSMENT";

    @Override
    public String getTableName() {
        return RESULT_RECIST_ASSESSMENT;
    }

    @Override
    public String getTablePrefix() {
        return "RCA";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_RECIST_ASSESSMENT).setTargetEntity("RESULT_PATIENT").addColumnToJoin("RCA_PAT_ID", "PAT_ID")
                .build());

        List<TableField> fieldValues = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("RCA_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_RECIST_ASSESSMENT, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT RCA_PAT_ID FROM RESULT_RECIST_ASSESSMENT e "
                + "left join RESULT_PATIENT p on (e.RCA_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_RECIST_ASSESSMENT;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("RCA_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_NEW_LES_SINCE_BASELINE").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_NEW_LES_SITE").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_ASSESSMENT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_VISIT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_ASSESS_FREQ").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_RECIST_RESPONSE").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_INVES_AGREES_WITH_RECIST").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_INVES_ASSESSMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_REASON_DIFFER").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("RCA_PAT_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, RecistAssessment entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getNewLesionsSinceBaseline());
        ps.setObject(paramIndex++, entity.getNewLesionSite());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getNewLesionDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setObject(paramIndex++, entity.getAssessmentFrequency());
        ps.setString(paramIndex++, entity.getOverallRecistResponse());
        ps.setObject(paramIndex++, entity.getInvAgreeWithRecistResponse());
        ps.setString(paramIndex++, entity.getInvOpinion());
        ps.setObject(paramIndex++, entity.getReasonAssessmentsDiffer());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getIdColumnName() {
        return "RCA_ID";
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, RecistAssessment entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getNewLesionsSinceBaseline());
        ps.setObject(paramIndex++, entity.getOverallRecistResponse());
        ps.setString(paramIndex++, entity.getInvAgreeWithRecistResponse());
        ps.setObject(paramIndex++, entity.getInvOpinion());
        ps.setString(paramIndex++, entity.getReasonAssessmentsDiffer());
        ps.setObject(paramIndex++, entity.getAssessmentFrequency());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_RECIST_ASSESSMENT;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("RCA_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_NEW_LES_SINCE_BASELINE").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_RECIST_RESPONSE").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_INVES_AGREES_WITH_RECIST").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_INVES_ASSESSMENT").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_REASON_DIFFER").build());
        fieldsToSet.add(fieldBuilder.setField("RCA_ASSESS_FREQ").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("RCA_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

}

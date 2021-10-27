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
import com.acuity.visualisations.model.output.entities.EDiary;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class EDiaryDao extends EntityDao<EDiary> {
    private static final String EDIARY_ID = "EDIARY_ID";
    private static final String EDIARY_PAT_ID = "EDIARY_PAT_ID";

    @Override
    protected String getIdColumnName() {
        return EDIARY_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_EDIARY";
    }

    @Override
    protected String getTablePrefix() {
        return "EDIARY";
    }

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(EDIARY_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_ASSESSMENT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_DRUG_INTAKE_TIME").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_DEVICE_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_ASSESSMENT_TIME_MORNING").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_PEF_MORNING").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_ASSESSMENT_TIME_EVENING").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_PEF_EVENING").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_ASTHMA_SCORE_NIGHT").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_ASTHMA_SCORE_DAY").build());
        fieldsToInsert.add(fieldBuilder.setField("EDIARY_WOKE_DUE_TO_ASTHMA").build());
        fieldsToInsert.add(fieldBuilder.setField(EDIARY_PAT_ID).build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, EDiary entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAssessmentDate()));
        ps.setString(paramIndex++, entity.getDrugIntakeTime());
        ps.setString(paramIndex++, entity.getDeviceType());
        ps.setString(paramIndex++, entity.getMorningAssessmentTime());
        ps.setBigDecimal(paramIndex++, entity.getPefMorning());
        ps.setString(paramIndex++, entity.getEveningAssessmentTime());
        ps.setBigDecimal(paramIndex++, entity.getPefEvening());
        ps.setObject(paramIndex++, entity.getAsthmaSymptomScoreNight());
        ps.setObject(paramIndex++, entity.getAsthmaSymptomScoreDay());
        ps.setString(paramIndex++, entity.getWokeDueToAsthma());
        ps.setString(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_DRUG_INTAKE_TIME").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_DEVICE_TYPE").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_ASSESSMENT_TIME_MORNING").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_PEF_MORNING").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_ASSESSMENT_TIME_EVENING").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_PEF_EVENING").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_ASTHMA_SCORE_NIGHT").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_ASTHMA_SCORE_DAY").build());
        fieldsToSet.add(fieldBuilder.setField("EDIARY_WOKE_DUE_TO_ASTHMA").build());
        fieldsToSet.add(fieldBuilder.setField(EDIARY_PAT_ID).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(EDIARY_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, EDiary entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getDrugIntakeTime());
        ps.setString(paramIndex++, entity.getDeviceType());
        ps.setString(paramIndex++, entity.getMorningAssessmentTime());
        ps.setBigDecimal(paramIndex++, entity.getPefMorning());
        ps.setString(paramIndex++, entity.getEveningAssessmentTime());
        ps.setBigDecimal(paramIndex++, entity.getPefEvening());
        ps.setObject(paramIndex++, entity.getAsthmaSymptomScoreNight());
        ps.setObject(paramIndex++, entity.getAsthmaSymptomScoreDay());
        ps.setString(paramIndex++, entity.getWokeDueToAsthma());
        ps.setString(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(getTableName()).setTargetEntity("RESULT_PATIENT").addColumnToJoin(EDIARY_PAT_ID, "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("EDIARY_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(getTableName(), hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT EDIARY_PAT_ID FROM RESULT_EDIARY e "
                + "left join RESULT_PATIENT p on (e.EDIARY_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

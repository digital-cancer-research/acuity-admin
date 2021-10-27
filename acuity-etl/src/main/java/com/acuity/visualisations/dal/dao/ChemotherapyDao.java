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
import com.acuity.visualisations.model.output.entities.Chemotherapy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

/**
 * Created by knml167 on 21/01/14.
 */
@Repository
public class ChemotherapyDao extends EntityDao<Chemotherapy> {
    private static final String RESULT_CHEMOTHERAPY = "RESULT_CHEMOTHERAPY";
    @Override
    public String getTableName() {
        return RESULT_CHEMOTHERAPY;
    }

    @Override
    public String getTablePrefix() {
        return "CHEMO";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_CHEMOTHERAPY).setTargetEntity("RESULT_PATIENT").addColumnToJoin("CHEMO_PAT_ID", "PAT_ID")
                .build());

        List<TableField> fieldValues = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add(getTablePrefix() + "_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        final String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_CHEMOTHERAPY, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT CHEMO_PAT_ID FROM RESULT_CHEMOTHERAPY chm "
                + "left join RESULT_PATIENT p on (chm.CHEMO_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_CHEMOTHERAPY;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_VISIT_DAT").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_PREFERRED_NAME_OF_MED").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_START_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_END_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_NUM_OF_CYCLES").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_CLASS").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_TREATMENT_STATUS").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_BEST_RESPONSE").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_REASON_FOR_FAILURE").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_TIME_STATUS").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_CONCOMITANT_THERAPY").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_NUM_OF_REGIMENTS").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_CANCER_THERAPY_AGENT").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_THERAPY_REASON").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_ROUTE").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_TREATMENT_CONTINUES").build());

        fieldsToInsert.add(fieldBuilder.setField("CHEMO_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("CHEMO_PAT_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Chemotherapy entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setString(paramIndex++, entity.getPreferredNameOfMed());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getChemoStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getChemoEndDate()));
        ps.setObject(paramIndex++, entity.getNumberOfCycles());
        ps.setString(paramIndex++, entity.getChemoClass());
        ps.setString(paramIndex++, entity.getTreatmentStatus());
        ps.setString(paramIndex++, entity.getBestResponse());
        ps.setString(paramIndex++, entity.getReasonForFailure());
        ps.setString(paramIndex++, entity.getChemoTimeStatus());
        ps.setString(paramIndex++, entity.getConcomitantTherapy());
        ps.setObject(paramIndex++, entity.getNumberOfPriorRegiments());
        ps.setString(paramIndex++, entity.getCancerTherapyAgent());
        ps.setString(paramIndex++, entity.getTherapyReason());
        ps.setString(paramIndex++, entity.getRoute());
        ps.setString(paramIndex++, entity.getTreatmentContinues());

        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getIdColumnName() {
        return getTablePrefix() + "_ID";
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Chemotherapy entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setString(paramIndex++, entity.getPreferredNameOfMed());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getChemoStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getChemoEndDate()));
        ps.setObject(paramIndex++, entity.getNumberOfCycles());
        ps.setString(paramIndex++, entity.getChemoClass());
        ps.setString(paramIndex++, entity.getTreatmentStatus());
        ps.setString(paramIndex++, entity.getBestResponse());
        ps.setString(paramIndex++, entity.getReasonForFailure());
        ps.setString(paramIndex++, entity.getChemoTimeStatus());
        ps.setString(paramIndex++, entity.getConcomitantTherapy());
        ps.setObject(paramIndex++, entity.getNumberOfPriorRegiments());
        ps.setString(paramIndex++, entity.getCancerTherapyAgent());
        ps.setString(paramIndex++, entity.getTherapyReason());
        ps.setString(paramIndex++, entity.getRoute());
        ps.setString(paramIndex++, entity.getTreatmentContinues());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_CHEMOTHERAPY;
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_VISIT").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_PREFERRED_NAME_OF_MED").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_START_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_END_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_NUM_OF_CYCLES").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_CLASS").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_TREATMENT_STATUS").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_BEST_RESPONSE").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_REASON_FOR_FAILURE").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_TIME_STATUS").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_CONCOMITANT_THERAPY").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_NUM_OF_REGIMENTS").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_CANCER_THERAPY_AGENT").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_THERAPY_REASON").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_ROUTE").build());
        fieldsToSet.add(fieldBuilder.setField("CHEMO_TREATMENT_CONTINUES").build());

        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(getTablePrefix() + "_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }
}

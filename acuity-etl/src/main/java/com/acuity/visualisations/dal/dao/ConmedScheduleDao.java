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
import com.acuity.visualisations.model.output.entities.ConcomitantMedSchedule;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class ConmedScheduleDao extends EntityDao<ConcomitantMedSchedule> {
    private static final String RESULT_CONMED_SCHEDULE = "RESULT_CONMED_SCHEDULE";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_CONMED_SCHEDULE;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("CMS_MED_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ATC_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ATC_CODE_TEXT").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DOSE").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_START_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_END_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DOSE_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_FREQUENCY").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_REASON").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DOSE_TOTAL").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_DOSE_UNIT_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_FREQUENCY_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ROUTE").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_THERAPY_REASON").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_REASON_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_PROPH_SPEC_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_AE_NUM").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_REASON_STOP").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_REASON_STOP_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_INF_BODY_SYS").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_INF_BODY_SYS_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ACTIVE_INGR_1").build());
        fieldsToInsert.add(fieldBuilder.setField("CMS_ACTIVE_INGR_2").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_CONMED_SCHEDULE;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("CMS_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_ATC_CODE").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_ATC_CODE_TEXT").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_DOSE").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_END_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_DOSE_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_FREQUENCY").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_REASON").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_DOSE_TOTAL").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_DOSE_UNIT_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_FREQUENCY_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_ROUTE").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_THERAPY_REASON").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_REASON_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_PROPH_SPEC_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_AE_NUM").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_REASON_STOP").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_REASON_STOP_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_INF_BODY_SYS").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_INF_BODY_SYS_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_ACTIVE_INGR_1").build());
        fieldsToSet.add(fieldBuilder.setField("CMS_ACTIVE_INGR_2").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("CMS_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, ConcomitantMedSchedule entity) throws SQLException {
        int parameter = 1;
        ps.setObject(parameter++, entity.getMedicineGuid());
        ps.setObject(parameter++, entity.getPatientGuid());
        ps.setString(parameter++, entity.getId());
        ps.setString(parameter++, entity.getSha1ForUniqueFields());
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setString(parameter++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(parameter++, entity.getAtcCode());
        ps.setString(parameter++, entity.getAtcCodeText());
        ps.setObject(parameter++, entity.getDose());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(parameter++, entity.getDoseUnit());
        ps.setString(parameter++, entity.getFrequency());
        ps.setString(parameter++, entity.getTreatmentReason());
        ps.setObject(parameter++, entity.getDoseTotal());
        ps.setString(parameter++, entity.getDoseUnitOther());
        ps.setString(parameter++, entity.getFrequencyOther());
        ps.setString(parameter++, entity.getRoute());
        ps.setString(parameter++, entity.getTherapyReason());
        ps.setString(parameter++, entity.getTherapyReasonOther());
        ps.setString(parameter++, entity.getProphylaxisSpecOther());
        ps.setObject(parameter++, entity.getAeNumber());
        ps.setString(parameter++, entity.getReasonStop());
        ps.setString(parameter++, entity.getReasonStopOther());
        ps.setString(parameter++, entity.getInfBodySys());
        ps.setString(parameter++, entity.getInfBodySysOther());
        ps.setString(parameter++, entity.getActiveIngredient1());
        ps.setString(parameter, entity.getActiveIngredient2());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, ConcomitantMedSchedule entity) throws SQLException {
        int parameter = 1;
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(parameter++, entity.getAtcCode());
        ps.setString(parameter++, entity.getAtcCodeText());
        ps.setObject(parameter++, entity.getDose());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(parameter++, entity.getDoseUnit());
        ps.setString(parameter++, entity.getFrequency());
        ps.setString(parameter++, entity.getTreatmentReason());
        ps.setObject(parameter++, entity.getDoseTotal());
        ps.setString(parameter++, entity.getDoseUnitOther());
        ps.setString(parameter++, entity.getFrequencyOther());
        ps.setString(parameter++, entity.getRoute());
        ps.setString(parameter++, entity.getTherapyReason());
        ps.setString(parameter++, entity.getTherapyReasonOther());
        ps.setString(parameter++, entity.getProphylaxisSpecOther());
        ps.setObject(parameter++, entity.getAeNumber());
        ps.setString(parameter++, entity.getReasonStop());
        ps.setString(parameter++, entity.getReasonStopOther());
        ps.setString(parameter++, entity.getInfBodySys());
        ps.setString(parameter++, entity.getInfBodySysOther());
        ps.setString(parameter++, entity.getActiveIngredient1());
        ps.setString(parameter++, entity.getActiveIngredient2());
        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "CMS_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_CONMED_SCHEDULE;
    }

    @Override
    public String getTablePrefix() {
        return "CMS";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain
                .add(builder.setSourceEntity(RESULT_CONMED_SCHEDULE).setTargetEntity("RESULT_PATIENT").addColumnToJoin("CMS_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("CMS_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_CONMED_SCHEDULE, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT CMS_PAT_ID FROM "
                + "RESULT_CONMED_SCHEDULE e "
                + "left join RESULT_PATIENT p on (e.CMS_PAT_ID = p.PAT_ID) "
                + "LEFT JOIN RESULT_STUDY s ON (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

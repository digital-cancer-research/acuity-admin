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
import com.acuity.visualisations.model.output.entities.MedDosingSchedule;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class MedDosingScheduleDao extends EntityDao<MedDosingSchedule> {
    private static final String RESULT_TRG_MED_DOS_SCHEDULE = "RESULT_TRG_MED_DOS_SCHEDULE";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_TRG_MED_DOS_SCHEDULE;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MDS_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DRUG").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DOSE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DOSE_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_START_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_END_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DOSING_FREQ_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DOSING_FREQ_RANK").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_FREQUENCY_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_FREQUENCY").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_ACTION_TAKEN").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_REASON_FOR_ACTION_TAKEN").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_COMMENT").build());

        fieldsToInsert.add(fieldBuilder.setField("mds_total_daily_dose").build());
        fieldsToInsert.add(fieldBuilder.setField("mds_cycle_delayed").build());
        fieldsToInsert.add(fieldBuilder.setField("mds_reason_cycle_delayed").build());
        fieldsToInsert.add(fieldBuilder.setField("mds_reason_cycle_delayed_oth").build());

        fieldsToInsert.add(fieldBuilder.setField("MDS_MED_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_MED_DICTIONARY_TEXT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_ATC_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_ATC_DICTIONARY_TEXT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_DRUG_PREF_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_MED_GROUP_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_ACTIVE_INGREDIENT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_STUDY_DRUG_CAT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_PLANNED_DOSE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_PLANNED_DOSE_UNITS").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_PLANNED_NO_DAYS_TRT").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_AE_NUM_ACT_TAKEN").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_FORMULATION").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_ROUTE").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_REASON_FOR_THERAPY").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_AE_NUM_TRT_CYCLE_DEL").build());


        fieldsToInsert.add(fieldBuilder.setField("MDS_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("MDS_PAT_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_TRG_MED_DOS_SCHEDULE;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField("MDS_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_DOSE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_DOSE_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_END_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_DOSING_FREQ_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_DOSING_FREQ_RANK").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_FREQUENCY").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_FREQUENCY_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_ACTION_TAKEN").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_REASON_FOR_ACTION_TAKEN").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_COMMENT").build());

        fieldsToSet.add(fieldBuilder.setField("mds_total_daily_dose").build());
        fieldsToSet.add(fieldBuilder.setField("mds_cycle_delayed").build());
        fieldsToSet.add(fieldBuilder.setField("mds_reason_cycle_delayed").build());
        fieldsToSet.add(fieldBuilder.setField("mds_reason_cycle_delayed_oth").build());

        fieldsToSet.add(fieldBuilder.setField("MDS_MED_CODE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_MED_DICTIONARY_TEXT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_ATC_CODE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_ATC_DICTIONARY_TEXT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_DRUG_PREF_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_MED_GROUP_NAME").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_ACTIVE_INGREDIENT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_STUDY_DRUG_CAT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_PLANNED_DOSE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_PLANNED_DOSE_UNITS").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_PLANNED_NO_DAYS_TRT").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_AE_NUM_ACT_TAKEN").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_FORMULATION").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_ROUTE").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_REASON_FOR_THERAPY").build());
        fieldsToSet.add(fieldBuilder.setField("MDS_AE_NUM_TRT_CYCLE_DEL").build());


        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("MDS_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, MedDosingSchedule entity) throws SQLException {
        int parameter = 1;
        ps.setString(parameter++, entity.getId());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(parameter++, entity.getDrug());
        ps.setObject(parameter++, entity.getDose());
        ps.setString(parameter++, entity.getDoseUnit());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(parameter++, entity.getFreqName());
        ps.setObject(parameter++, entity.getFreqRank());
        ps.setString(parameter++, entity.getFrequencyUnit());
        ps.setObject(parameter++, entity.getFrequency());
        ps.setString(parameter++, entity.getActionTaken());
        ps.setString(parameter++, entity.getReasonForActionTaken());
        ps.setString(parameter++, entity.getComment());

        ps.setObject(parameter++, entity.getTotalDailyDose());
        ps.setString(parameter++, entity.getTreatmentCycleDelayed());
        ps.setString(parameter++, entity.getReasonTreatmentCycleDelayed());
        ps.setString(parameter++, entity.getReasonTreatmentCycleDelayedOther());

        ps.setString(parameter++, entity.getMedicationCode());
        ps.setString(parameter++, entity.getMedicationDictionaryText());
        ps.setString(parameter++, entity.getAtcCode());
        ps.setString(parameter++, entity.getAtcDictionaryText());
        ps.setString(parameter++, entity.getDrugPreferredName());
        ps.setString(parameter++, entity.getMedicationGroupingName());
        ps.setString(parameter++, entity.getActiveIngredientOutput());
        ps.setString(parameter++, entity.getStudyDrugCategory());
        ps.setObject(parameter++, entity.getPlannedDose());
        ps.setString(parameter++, entity.getPlannedDoseUnits());
        ps.setObject(parameter++, entity.getPlannedNoDaysTreatment());
        ps.setString(parameter++, entity.getAeNumsCausedActionTaken());
        ps.setString(parameter++, entity.getFormulation());
        ps.setString(parameter++, entity.getRoute());
        ps.setString(parameter++, entity.getReasonForTherapy());
        ps.setString(parameter++, entity.getAeNumsCausedCycleDelayed());

        ps.setString(parameter++, entity.getSha1ForUniqueFields());
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setString(parameter++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(parameter, entity.getPatientGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, MedDosingSchedule entity) throws SQLException {
        int parameter = 1;
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(parameter++, entity.getDose());
        ps.setString(parameter++, entity.getDoseUnit());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getEndDate()));
        ps.setObject(parameter++, entity.getFreqName());
        ps.setObject(parameter++, entity.getFreqRank());
        ps.setObject(parameter++, entity.getFrequency());
        ps.setString(parameter++, entity.getFrequencyUnit());
        ps.setString(parameter++, entity.getActionTaken());
        ps.setString(parameter++, entity.getReasonForActionTaken());
        ps.setString(parameter++, entity.getComment());

        ps.setObject(parameter++, entity.getTotalDailyDose());
        ps.setString(parameter++, entity.getTreatmentCycleDelayed());
        ps.setString(parameter++, entity.getReasonTreatmentCycleDelayed());
        ps.setString(parameter++, entity.getReasonTreatmentCycleDelayedOther());

        ps.setString(parameter++, entity.getMedicationCode());
        ps.setString(parameter++, entity.getMedicationDictionaryText());
        ps.setString(parameter++, entity.getAtcCode());
        ps.setString(parameter++, entity.getAtcDictionaryText());
        ps.setString(parameter++, entity.getDrugPreferredName());
        ps.setString(parameter++, entity.getMedicationGroupingName());
        ps.setString(parameter++, entity.getActiveIngredientOutput());
        ps.setString(parameter++, entity.getStudyDrugCategory());
        ps.setObject(parameter++, entity.getPlannedDose());
        ps.setString(parameter++, entity.getPlannedDoseUnits());
        ps.setObject(parameter++, entity.getPlannedNoDaysTreatment());
        ps.setString(parameter++, entity.getAeNumsCausedActionTaken());
        ps.setString(parameter++, entity.getFormulation());
        ps.setString(parameter++, entity.getRoute());
        ps.setString(parameter++, entity.getReasonForTherapy());
        ps.setString(parameter++, entity.getAeNumsCausedCycleDelayed());

        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "MDS_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_TRG_MED_DOS_SCHEDULE;
    }

    @Override
    public String getTablePrefix() {
        return "MDS";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_TRG_MED_DOS_SCHEDULE).setTargetEntity("RESULT_PATIENT")
                .addColumnToJoin("MDS_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("MDS_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_TRG_MED_DOS_SCHEDULE, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT MDS_PAT_ID FROM RESULT_TRG_MED_DOS_SCHEDULE e "
                + "left join RESULT_PATIENT p on (e.MDS_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

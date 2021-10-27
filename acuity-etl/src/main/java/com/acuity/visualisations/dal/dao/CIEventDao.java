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
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.CIEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class CIEventDao extends EntityDao<CIEvent> {
    private static final String TABLE = "RESULT_CI_EVENT";
    private static final String PREFIX = "ci";
    private static final String ID = PREFIX + "_id";


    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public String getTablePrefix() {
        return PREFIX;
    }

    @Override
    protected String getIdColumnName() {
        return ID;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select distinct pat_id "
                + " from " + TABLE
                + " join result_patient ON pat_id=" + PREFIX + "_pat_id "
                + " join result_study ON std_id=pat_std_id "
                + " where std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select " + PREFIX + "_unq_sha1, " + PREFIX + "_sec_hash, " + ID
                + " from " + TABLE
                + " join result_patient on pat_id=" + PREFIX + "_pat_id "
                + " where pat_std_id=?";
    }


    @Override
    protected String getInsertStatement() {
        String targetTable = TABLE;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField(PREFIX + "_id").build(),
                fieldBuilder.setField(PREFIX + "_unq_sha1").build(),
                fieldBuilder.setField(PREFIX + "_sec_hash").build(),
                fieldBuilder.setField(PREFIX + "_date_created").build(),
                fieldBuilder.setField(PREFIX + "_date_updated").build(),
                fieldBuilder.setField(PREFIX + "_pat_id").build(),

                fieldBuilder.setField("ci_start_date").build(),
                fieldBuilder.setField("ci_ae_num").build(),
                fieldBuilder.setField("ci_event_term").build(),
                fieldBuilder.setField("ci_ischemic_symptoms").build(),
                fieldBuilder.setField("ci_symptoms_duration").build(),
                fieldBuilder.setField("ci_symptoms_prompt_uns_hosp").build(),
                fieldBuilder.setField("ci_event_due_to_stent_thromb").build(),
                fieldBuilder.setField("ci_prev_ecg_available").build(),
                fieldBuilder.setField("ci_prev_ecg_date").build(),
                fieldBuilder.setField("ci_ecg_at_event_time").build(),
                fieldBuilder.setField("ci_no_ecg_at_event_time").build(),
                fieldBuilder.setField("ci_local_card_biom_drawn").build(),
                fieldBuilder.setField("ci_coron_angiography_perf").build(),
                fieldBuilder.setField("ci_date_of_angiogr").build(),
                fieldBuilder.setField("ci_fin_diagnosis").build(),
                fieldBuilder.setField("ci_oth_diagnosis").build(),
                fieldBuilder.setField("ci_description1").build(),
                fieldBuilder.setField("ci_description2").build(),
                fieldBuilder.setField("ci_description3").build(),
                fieldBuilder.setField("ci_description4").build(),
                fieldBuilder.setField("ci_description5").build()
        );

        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = TABLE;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField(PREFIX + "_sec_hash").build(),
                fieldBuilder.setField(PREFIX + "_date_updated").build(),

                fieldBuilder.setField("ci_start_date").build(),
                fieldBuilder.setField("ci_ae_num").build(),
                fieldBuilder.setField("ci_event_term").build(),
                fieldBuilder.setField("ci_ischemic_symptoms").build(),
                fieldBuilder.setField("ci_symptoms_duration").build(),
                fieldBuilder.setField("ci_symptoms_prompt_uns_hosp").build(),
                fieldBuilder.setField("ci_event_due_to_stent_thromb").build(),
                fieldBuilder.setField("ci_prev_ecg_available").build(),
                fieldBuilder.setField("ci_prev_ecg_date").build(),
                fieldBuilder.setField("ci_ecg_at_event_time").build(),
                fieldBuilder.setField("ci_no_ecg_at_event_time").build(),
                fieldBuilder.setField("ci_local_card_biom_drawn").build(),
                fieldBuilder.setField("ci_coron_angiography_perf").build(),
                fieldBuilder.setField("ci_date_of_angiogr").build(),
                fieldBuilder.setField("ci_fin_diagnosis").build(),
                fieldBuilder.setField("ci_oth_diagnosis").build(),
                fieldBuilder.setField("ci_description1").build(),
                fieldBuilder.setField("ci_description2").build(),
                fieldBuilder.setField("ci_description3").build(),
                fieldBuilder.setField("ci_description4").build(),
                fieldBuilder.setField("ci_description5").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
    }


    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, CIEvent entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setObject(paramIndex++, entity.getAeNumber());
        ps.setString(paramIndex++, entity.getEventTerm());
        ps.setString(paramIndex++, entity.getIschemicSymptoms());
        ps.setString(paramIndex++, entity.getCieSymptomsDuration());
        ps.setString(paramIndex++, entity.getSymptCausedUnscheduledHosp());
        ps.setString(paramIndex++, entity.getSymptCausedStentThromb());
        ps.setString(paramIndex++, entity.getPrevEcgBeforeAvailableEvent());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getPrevEcgDate()));
        ps.setString(paramIndex++, entity.getAvailableEcgAtEventTime());
        ps.setString(paramIndex++, entity.getNoEcgAtEventTime());
        ps.setString(paramIndex++, entity.getLocalCardiacBiomarkersDrawn());
        ps.setString(paramIndex++, entity.getCoronaryAngiographyPerformed());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAngiographyDate()));
        ps.setString(paramIndex++, entity.getFinalDiagnosis());
        ps.setString(paramIndex++, entity.getOtherDiagnosis());
        ps.setString(paramIndex++, entity.getDescription1());
        ps.setString(paramIndex++, entity.getDescription2());
        ps.setString(paramIndex++, entity.getDescription3());
        ps.setString(paramIndex++, entity.getDescription4());
        ps.setString(paramIndex, entity.getDescription5());

    }


    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, CIEvent entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setObject(paramIndex++, entity.getAeNumber());
        ps.setString(paramIndex++, entity.getEventTerm());
        ps.setString(paramIndex++, entity.getIschemicSymptoms());
        ps.setString(paramIndex++, entity.getCieSymptomsDuration());
        ps.setString(paramIndex++, entity.getSymptCausedUnscheduledHosp());
        ps.setString(paramIndex++, entity.getSymptCausedStentThromb());
        ps.setString(paramIndex++, entity.getPrevEcgBeforeAvailableEvent());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getPrevEcgDate()));
        ps.setString(paramIndex++, entity.getAvailableEcgAtEventTime());
        ps.setString(paramIndex++, entity.getNoEcgAtEventTime());
        ps.setString(paramIndex++, entity.getLocalCardiacBiomarkersDrawn());
        ps.setString(paramIndex++, entity.getCoronaryAngiographyPerformed());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAngiographyDate()));
        ps.setString(paramIndex++, entity.getFinalDiagnosis());
        ps.setString(paramIndex++, entity.getOtherDiagnosis());
        ps.setString(paramIndex++, entity.getDescription1());
        ps.setString(paramIndex++, entity.getDescription2());
        ps.setString(paramIndex++, entity.getDescription3());
        ps.setString(paramIndex++, entity.getDescription4());
        ps.setString(paramIndex, entity.getDescription5());
    }
}

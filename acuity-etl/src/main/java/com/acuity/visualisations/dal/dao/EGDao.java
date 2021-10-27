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
import com.acuity.visualisations.model.output.entities.EG;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class EGDao extends EntityDao<EG> {
    private static final String RESULT_EG = "result_eg";
    private static final String EG_ID = "eg_id";

    @Override
    public String getTableName() {
        return RESULT_EG;
    }

    @Override
    protected String getTablePrefix() {
        return "eg";
    }

    @Override
    protected String getIdColumnName() {
        return EG_ID;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT pat_id "
                + " FROM result_eg "
                + " JOIN result_test ON tst_id=eg_tst_id "
                + " JOIN result_patient ON pat_id=tst_pat_id "
                + " JOIN result_study ON std_id=pat_std_id "
                + " WHERE std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return "select eg_unq_sha1, eg_sec_hash, eg_id "
                + "from result_eg "
                + "join result_test on tst_id=eg_tst_id "
                + "join result_patient on pat_id=tst_pat_id "
                + "where pat_std_id=?";
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_EG;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField(EG_ID).build(),
                fieldBuilder.setField("eg_unq_sha1").build(),
                fieldBuilder.setField("eg_sec_hash").build(),
                fieldBuilder.setField("eg_ref_sha1").build(),
                fieldBuilder.setField("eg_date_created").build(),
                fieldBuilder.setField("eg_date_updated").build(),

                fieldBuilder.setField("eg_test_name").build(),
                fieldBuilder.setField("eg_test_result").build(),
                fieldBuilder.setField("eg_result_unit").build(),

                fieldBuilder.setField("eg_evaluation").build(),
                fieldBuilder.setField("eg_abnormality").build(),
                fieldBuilder.setField("eg_significant").build(),
                fieldBuilder.setField("eg_sch_timepoint").build(),

                fieldBuilder.setField("eg_date_last_dose").build(),
                fieldBuilder.setField("eg_last_dose_amount").build(),
                fieldBuilder.setField("eg_method").build(),
                fieldBuilder.setField("eg_atrial_fibr").build(),
                fieldBuilder.setField("eg_sinus_rhythm").build(),
                fieldBuilder.setField("eg_reas_no_sinus_rhythm").build(),
                fieldBuilder.setField("eg_heart_rhythm").build(),
                fieldBuilder.setField("eg_heart_rhythm_oth").build(),
                fieldBuilder.setField("eg_extra_systoles").build(),
                fieldBuilder.setField("eg_specify_extra_systoles").build(),
                fieldBuilder.setField("eg_type_cond").build(),
                fieldBuilder.setField("eg_cond").build(),
                fieldBuilder.setField("eg_reas_abnormal_cond").build(),
                fieldBuilder.setField("eg_stt_changes").build(),
                fieldBuilder.setField("eg_st_segment").build(),
                fieldBuilder.setField("eg_wave").build(),

                fieldBuilder.setField("eg_tst_id").build()
        );

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, EG entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getTestName());
        ps.setObject(paramIndex++, entity.getTestResult());
        ps.setString(paramIndex++, entity.getResultUnit());
        ps.setString(paramIndex++, entity.getEvaluation());
        ps.setString(paramIndex++, entity.getAbnormality());
        ps.setString(paramIndex++, entity.getSignificant());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimePoint());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateOfLastDose()));
        ps.setString(paramIndex++, entity.getLastDoseAmount());
        ps.setString(paramIndex++, entity.getMethod());
        ps.setString(paramIndex++, entity.getAtrialFibrillation());
        ps.setString(paramIndex++, entity.getSinusRhythm());
        ps.setString(paramIndex++, entity.getReasonNoSinusRhythm());
        ps.setString(paramIndex++, entity.getHeartRhythm());
        ps.setString(paramIndex++, entity.getHeartRhythmOther());
        ps.setString(paramIndex++, entity.getExtraSystoles());
        ps.setString(paramIndex++, entity.getSpecifyExtraSystoles());
        ps.setString(paramIndex++, entity.getTypeOfConduction());
        ps.setString(paramIndex++, entity.getConduction());
        ps.setString(paramIndex++, entity.getReasonAbnormalConduction());
        ps.setString(paramIndex++, entity.getSttChanges());
        ps.setString(paramIndex++, entity.getStSegment());
        ps.setString(paramIndex++, entity.getTWave());

        ps.setString(paramIndex, entity.getTestGuid());
    }


    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, EG entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex++, entity.getTestResult());
        ps.setString(paramIndex++, entity.getEvaluation());
        ps.setString(paramIndex++, entity.getAbnormality());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimePoint());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateOfLastDose()));
        ps.setString(paramIndex++, entity.getLastDoseAmount());
        ps.setString(paramIndex++, entity.getMethod());
        ps.setString(paramIndex++, entity.getAtrialFibrillation());
        ps.setString(paramIndex++, entity.getSinusRhythm());
        ps.setString(paramIndex++, entity.getReasonNoSinusRhythm());
        ps.setString(paramIndex++, entity.getHeartRhythm());
        ps.setString(paramIndex++, entity.getHeartRhythmOther());
        ps.setString(paramIndex++, entity.getExtraSystoles());
        ps.setString(paramIndex++, entity.getSpecifyExtraSystoles());
        ps.setString(paramIndex++, entity.getTypeOfConduction());
        ps.setString(paramIndex++, entity.getConduction());
        ps.setString(paramIndex++, entity.getReasonAbnormalConduction());
        ps.setString(paramIndex++, entity.getSttChanges());
        ps.setString(paramIndex++, entity.getStSegment());
        ps.setString(paramIndex++, entity.getTWave());

        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_EG;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("eg_sec_hash").build(),
                fieldBuilder.setField("eg_date_updated").build(),
                fieldBuilder.setField("eg_test_result").build(),
                fieldBuilder.setField("eg_evaluation").build(),
                fieldBuilder.setField("eg_abnormality").build(),
                fieldBuilder.setField("eg_sch_timepoint").build(),

                fieldBuilder.setField("eg_date_last_dose").build(),
                fieldBuilder.setField("eg_last_dose_amount").build(),
                fieldBuilder.setField("eg_method").build(),
                fieldBuilder.setField("eg_atrial_fibr").build(),
                fieldBuilder.setField("eg_sinus_rhythm").build(),
                fieldBuilder.setField("eg_reas_no_sinus_rhythm").build(),
                fieldBuilder.setField("eg_heart_rhythm").build(),
                fieldBuilder.setField("eg_heart_rhythm_oth").build(),
                fieldBuilder.setField("eg_extra_systoles").build(),
                fieldBuilder.setField("eg_specify_extra_systoles").build(),
                fieldBuilder.setField("eg_type_cond").build(),
                fieldBuilder.setField("eg_cond").build(),
                fieldBuilder.setField("eg_reas_abnormal_cond").build(),
                fieldBuilder.setField("eg_stt_changes").build(),
                fieldBuilder.setField("eg_st_segment").build(),
                fieldBuilder.setField("eg_wave").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(EG_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

}

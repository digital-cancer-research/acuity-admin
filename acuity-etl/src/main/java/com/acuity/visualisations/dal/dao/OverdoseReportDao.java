
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
import com.acuity.visualisations.model.output.entities.OverdoseReport;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class OverdoseReportDao extends EntityDao<OverdoseReport> {
    private static final String TABLE = "RESULT_OVERDOSE_REPORT";
    private static final String PREFIX = "or";
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

                fieldBuilder.setField("or_drug").build(),
                fieldBuilder.setField("or_route").build(),
                fieldBuilder.setField("or_start_date").build(),
                fieldBuilder.setField("or_stop_date").build(),
                fieldBuilder.setField("or_intent_flag").build(),
                fieldBuilder.setField("or_overdose_ae_flag").build(),
                fieldBuilder.setField("or_num_for_ae").build(),
                fieldBuilder.setField("or_ae_overdose_ass").build(),
                fieldBuilder.setField("or_total_dose").build(),
                fieldBuilder.setField("or_total_dose_unit").build(),
                fieldBuilder.setField("or_further_info").build()


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

                fieldBuilder.setField("or_drug").build(),
                fieldBuilder.setField("or_route").build(),
                fieldBuilder.setField("or_start_date").build(),
                fieldBuilder.setField("or_stop_date").build(),
                fieldBuilder.setField("or_intent_flag").build(),
                fieldBuilder.setField("or_overdose_ae_flag").build(),
                fieldBuilder.setField("or_num_for_ae").build(),
                fieldBuilder.setField("or_ae_overdose_ass").build(),
                fieldBuilder.setField("or_total_dose").build(),
                fieldBuilder.setField("or_total_dose_unit").build(),
                fieldBuilder.setField("or_further_info").build()

        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
    }


    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, OverdoseReport entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setString(paramIndex++, entity.getDrug());
        ps.setString(paramIndex++, entity.getRoute());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStopDate()));
        ps.setString(paramIndex++, entity.getIntentFlag());
        ps.setString(paramIndex++, entity.getOverdoseAeFlag());
        ps.setObject(paramIndex++, entity.getNumberForAe());
        ps.setString(paramIndex++, entity.getAeOverdoseAss());
        ps.setObject(paramIndex++, entity.getTotalDose());
        ps.setString(paramIndex++, entity.getTotalDoseUnit());
        ps.setString(paramIndex, entity.getFurtherInfo());

    }


    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, OverdoseReport entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getDrug());
        ps.setString(paramIndex++, entity.getRoute());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStopDate()));
        ps.setString(paramIndex++, entity.getIntentFlag());
        ps.setString(paramIndex++, entity.getOverdoseAeFlag());
        ps.setObject(paramIndex++, entity.getNumberForAe());
        ps.setString(paramIndex++, entity.getAeOverdoseAss());
        ps.setObject(paramIndex++, entity.getTotalDose());
        ps.setString(paramIndex++, entity.getTotalDoseUnit());
        ps.setString(paramIndex, entity.getFurtherInfo());

    }
}

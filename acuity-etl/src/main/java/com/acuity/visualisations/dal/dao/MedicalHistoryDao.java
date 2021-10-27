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
import com.acuity.visualisations.model.output.entities.MedicalHistory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class MedicalHistoryDao extends EntityDao<MedicalHistory> {

    @Override
    public String getTableName() {
        return "RESULT_MEDICAL_HISTORY";
    }

    @Override
    protected String getTablePrefix() {
        return "mh";
    }

    @Override
    protected String getIdColumnName() {
        return "mh_id";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT pat_id "
                + " FROM RESULT_MEDICAL_HISTORY "
                + " JOIN result_patient ON pat_id=mh_pat_id "
                + " JOIN result_study ON std_id=pat_std_id "
                + " WHERE std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select mh_unq_sha1, mh_sec_hash, mh_id "
                + " from RESULT_MEDICAL_HISTORY "
                + " join result_patient on pat_id=mh_pat_id "
                + " where pat_std_id=?";
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = "RESULT_MEDICAL_HISTORY";
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("mh_id").build(),
                fieldBuilder.setField("mh_unq_sha1").build(),
                fieldBuilder.setField("mh_sec_hash").build(),
                fieldBuilder.setField("mh_date_created").build(),
                fieldBuilder.setField("mh_date_updated").build(),
                fieldBuilder.setField("mh_pat_id").build(),
                fieldBuilder.setField("MH_START_DATE").build(),
                fieldBuilder.setField("MH_END_DATE").build(),
                fieldBuilder.setField("MH_CATEGORY").build(),
                fieldBuilder.setField("MH_TERM").build(),
                fieldBuilder.setField("MH_CONDITION_STATUS").build(),
                fieldBuilder.setField("MH_LLT_NAME").build(),
                fieldBuilder.setField("MH_PT_NAME").build(),
                fieldBuilder.setField("MH_HLT_NAME").build(),
                fieldBuilder.setField("MH_SOC_NAME").build(),
                fieldBuilder.setField("MH_CURRENT_MEDICATION").build()

        );

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, MedicalHistory entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(paramIndex++, entity.getCategory());
        ps.setString(paramIndex++, entity.getTerm());
        ps.setString(paramIndex++, entity.getConditionStatus());
        ps.setString(paramIndex++, entity.getLltName());
        ps.setString(paramIndex++, entity.getPtName());
        ps.setString(paramIndex++, entity.getHltName());
        ps.setString(paramIndex++, entity.getSocName());
        ps.setString(paramIndex, entity.getCurrentMedication());
    }


    @Override
    protected String getUpdateStatement() {
        String tagetTable = "RESULT_MEDICAL_HISTORY";
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("mh_sec_hash").build(),
                fieldBuilder.setField("mh_date_updated").build(),
                fieldBuilder.setField("MH_START_DATE").build(),
                fieldBuilder.setField("MH_END_DATE").build(),
                fieldBuilder.setField("MH_CATEGORY").build(),
                fieldBuilder.setField("MH_TERM").build(),
                fieldBuilder.setField("MH_CONDITION_STATUS").build(),
                fieldBuilder.setField("MH_LLT_NAME").build(),
                fieldBuilder.setField("MH_PT_NAME").build(),
                fieldBuilder.setField("MH_HLT_NAME").build(),
                fieldBuilder.setField("MH_SOC_NAME").build(),
                fieldBuilder.setField("MH_CURRENT_MEDICATION").build()

        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField("mh_id").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, MedicalHistory entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(paramIndex++, entity.getCategory());
        ps.setString(paramIndex++, entity.getTerm());
        ps.setString(paramIndex++, entity.getConditionStatus());
        ps.setString(paramIndex++, entity.getLltName());
        ps.setString(paramIndex++, entity.getPtName());
        ps.setString(paramIndex++, entity.getHltName());
        ps.setString(paramIndex++, entity.getSocName());
        ps.setString(paramIndex++, entity.getCurrentMedication());
        ps.setString(paramIndex, entity.getId());
    }
}

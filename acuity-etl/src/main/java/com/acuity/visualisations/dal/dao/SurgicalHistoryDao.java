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
import com.acuity.visualisations.model.output.entities.SurgicalHistory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class SurgicalHistoryDao extends EntityDao<SurgicalHistory> {

    @Override
    public String getTableName() {
        return "result_surgical_history";
    }

    @Override
    protected String getTablePrefix() {
        return "sh";
    }

    @Override
    protected String getIdColumnName() {
        return "sh_id";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT pat_id "
                + " FROM result_surgical_history "
                + " JOIN result_patient ON pat_id=sh_pat_id "
                + " JOIN result_study ON std_id=pat_std_id "
                + " WHERE std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return ""
                + " select sh_unq_sha1, sh_sec_hash, sh_id "
                + " from result_surgical_history "
                + " join result_patient on pat_id=sh_pat_id "
                + " where pat_std_id=?";
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = getTableName();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("sh_id").build(),
                fieldBuilder.setField("sh_unq_sha1").build(),
                fieldBuilder.setField("sh_sec_hash").build(),
                fieldBuilder.setField("sh_date_created").build(),
                fieldBuilder.setField("sh_date_updated").build(),
                fieldBuilder.setField("sh_pat_id").build(),

                fieldBuilder.setField("sh_start_date").build(),
                fieldBuilder.setField("sh_procedure").build(),
                fieldBuilder.setField("sh_hlt").build(),
                fieldBuilder.setField("sh_llt").build(),
                fieldBuilder.setField("sh_pt").build(),
                fieldBuilder.setField("sh_soc").build(),
                fieldBuilder.setField("sh_current").build()
        );

        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, SurgicalHistory entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setString(paramIndex++, entity.getProcedure());
        ps.setString(paramIndex++, entity.getHlt());
        ps.setString(paramIndex++, entity.getLlt());
        ps.setString(paramIndex++, entity.getPt());
        ps.setString(paramIndex++, entity.getSoc());
        ps.setString(paramIndex, entity.getCurrent());
    }


    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("sh_sec_hash").build(),
                fieldBuilder.setField("sh_date_updated").build(),

                fieldBuilder.setField("sh_start_date").build(),
                fieldBuilder.setField("sh_procedure").build(),
                fieldBuilder.setField("sh_hlt").build(),
                fieldBuilder.setField("sh_llt").build(),
                fieldBuilder.setField("sh_pt").build(),
                fieldBuilder.setField("sh_soc").build(),
                fieldBuilder.setField("sh_current").build()

        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField("sh_id").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, SurgicalHistory entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setString(paramIndex++, entity.getProcedure());
        ps.setString(paramIndex++, entity.getHlt());
        ps.setString(paramIndex++, entity.getLlt());
        ps.setString(paramIndex++, entity.getPt());
        ps.setString(paramIndex++, entity.getSoc());
        ps.setString(paramIndex++, entity.getCurrent());

        ps.setString(paramIndex, entity.getId());
    }
}

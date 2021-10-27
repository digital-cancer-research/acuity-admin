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
import com.acuity.visualisations.model.output.entities.CtDna;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class CtDnaDao extends EntityDao<CtDna> {


    @Override
    protected String getInsertStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("CTD_ID").build(),
                fieldBuilder.setField("CTD_DATE_CREATED").build(),
                fieldBuilder.setField("CTD_DATE_UPDATED").build(),
                fieldBuilder.setField("CTD_UNQ_SHA1").build(),
                fieldBuilder.setField("CTD_SEC_HASH").build(),
                fieldBuilder.setField("CTD_PAT_ID").build(),

                fieldBuilder.setField("CTD_GENE").build(),
                fieldBuilder.setField("CTD_MUTATION").build(),
                fieldBuilder.setField("CTD_SAMPLE_DATE").build(),
                fieldBuilder.setField("CTD_REPORTED_VAR_ALLELE_FREQ").build(),

                fieldBuilder.setField("CTD_TRACKED_MUTATION").build(),
                fieldBuilder.setField("CTD_VISIT_NAME").build(),
                fieldBuilder.setField("CTD_VISIT_NUMBER").build()
        );

        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, CtDna entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setString(paramIndex++, entity.getGene());
        ps.setString(paramIndex++, entity.getMutation());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSampleDate()));
        ps.setObject(paramIndex++, entity.getReportedVAF());

        ps.setString(paramIndex++, entity.getTrackedMutation());
        ps.setString(paramIndex++, entity.getVisitName());
        ps.setBigDecimal(paramIndex, entity.getVisitNumber());
    }

    @Override
    protected String getIdColumnName() {
        return "CTD_ID";
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, CtDna entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getGene());
        ps.setString(paramIndex++, entity.getMutation());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSampleDate()));
        ps.setObject(paramIndex++, entity.getReportedVAF());

        ps.setString(paramIndex++, entity.getTrackedMutation());
        ps.setString(paramIndex++, entity.getVisitName());
        ps.setBigDecimal(paramIndex++, entity.getVisitNumber());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("CTD_SEC_HASH").build(),
                fieldBuilder.setField("CTD_DATE_UPDATED").build(),

                fieldBuilder.setField("CTD_GENE").build(),
                fieldBuilder.setField("CTD_MUTATION").build(),
                fieldBuilder.setField("CTD_SAMPLE_DATE").build(),
                fieldBuilder.setField("CTD_REPORTED_VAR_ALLELE_FREQ").build(),

                fieldBuilder.setField("CTD_TRACKED_MUTATION").build(),
                fieldBuilder.setField("CTD_VISIT_NAME").build(),
                fieldBuilder.setField("CTD_VISIT_NUMBER").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(getIdColumnName()).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    public String getTableName() {
        return "RESULT_CTDNA";
    }

    @Override
    protected String getTablePrefix() {
        return "CTD";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select " + getTablePrefix() + "_unq_sha1, " + getTablePrefix() + "_sec_hash, " + getIdColumnName()
                + " from " + getTableName()
                + " join result_patient on pat_id=" + getTablePrefix() + "_pat_id "
                + " where pat_std_id=?";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select distinct pat_id "
                + " from " + getTableName()
                + " join result_patient ON pat_id=" + getTablePrefix() + "_pat_id "
                + " join result_study ON std_id=pat_std_id "
                + " where std_name=?", String.class, studyName);
    }
}

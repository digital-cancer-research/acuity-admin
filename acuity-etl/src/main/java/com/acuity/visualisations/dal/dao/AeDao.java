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
import com.acuity.visualisations.model.output.entities.AE;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class AeDao extends EntityDao<AE> {
    private static final String AE_ID = "AE_ID";

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(AE_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("AE_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("AE_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_TEXT").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_COMMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_SERIOUS").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_NUMBER").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_EVT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("AE_OUTCOME").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_DOSE_LIMITING_TOXICITY").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_TIME_POINT").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_IMMUNE_MEDIATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_INFUSION_REACTION").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_REQUIRED_TREATMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_CAUSED_SUBJECT_WITHDRAWAL").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_SUSPECTED_ENDPOINT").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_SUSPECTED_ENDPOINT_CATEGORY").build());
        fieldsToInsert.add(fieldBuilder.setField("AE_OF_SPECIAL_INTEREST").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("AE_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("AE_TEXT").build());
        fieldsToSet.add(fieldBuilder.setField("AE_COMMENT").build());
        fieldsToSet.add(fieldBuilder.setField("AE_SERIOUS").build());
        fieldsToSet.add(fieldBuilder.setField("AE_NUMBER").build());

        fieldsToSet.add(fieldBuilder.setField("AE_OUTCOME").build());
        fieldsToSet.add(fieldBuilder.setField("AE_DOSE_LIMITING_TOXICITY").build());
        fieldsToSet.add(fieldBuilder.setField("AE_TIME_POINT").build());
        fieldsToSet.add(fieldBuilder.setField("AE_IMMUNE_MEDIATED").build());
        fieldsToSet.add(fieldBuilder.setField("AE_INFUSION_REACTION").build());
        fieldsToSet.add(fieldBuilder.setField("AE_REQUIRED_TREATMENT").build());
        fieldsToSet.add(fieldBuilder.setField("AE_CAUSED_SUBJECT_WITHDRAWAL").build());
        fieldsToSet.add(fieldBuilder.setField("AE_SUSPECTED_ENDPOINT").build());
        fieldsToSet.add(fieldBuilder.setField("AE_SUSPECTED_ENDPOINT_CATEGORY").build());
        fieldsToSet.add(fieldBuilder.setField("AE_OF_SPECIAL_INTEREST").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(AE_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, AE entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAeText());
        ps.setString(paramIndex++, entity.getComment());
        ps.setObject(paramIndex++, entity.getSerious());
        ps.setObject(paramIndex++, entity.getNumber());
        ps.setObject(paramIndex++, entity.getEventTypeGuid());
        ps.setObject(paramIndex++, entity.getPatientGuid());

        ps.setObject(paramIndex++, entity.getOutcome());
        ps.setObject(paramIndex++, entity.getDoseLimitingToxicity());
        ps.setObject(paramIndex++, entity.getTimePoint());
        ps.setObject(paramIndex++, entity.getImmuneMediated());
        ps.setObject(paramIndex++, entity.getInfusionReaction());
        ps.setObject(paramIndex++, entity.getRequiredTreatment());
        ps.setObject(paramIndex++, entity.getCausedSubjectWithdrawal());
        ps.setObject(paramIndex++, entity.getSuspectedEndpoint());
        ps.setObject(paramIndex++, entity.getSuspectedEndpointCategory());
        ps.setObject(paramIndex, entity.getAeOfSpecialInterest());

    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, AE entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAeText());
        ps.setString(paramIndex++, entity.getComment());
        ps.setObject(paramIndex++, entity.getSerious());
        ps.setObject(paramIndex++, entity.getNumber());

        ps.setObject(paramIndex++, entity.getOutcome());
        ps.setObject(paramIndex++, entity.getDoseLimitingToxicity());
        ps.setObject(paramIndex++, entity.getTimePoint());
        ps.setObject(paramIndex++, entity.getImmuneMediated());
        ps.setObject(paramIndex++, entity.getInfusionReaction());
        ps.setObject(paramIndex++, entity.getRequiredTreatment());
        ps.setObject(paramIndex++, entity.getCausedSubjectWithdrawal());
        ps.setObject(paramIndex++, entity.getSuspectedEndpoint());
        ps.setObject(paramIndex++, entity.getSuspectedEndpointCategory());
        ps.setObject(paramIndex++, entity.getAeOfSpecialInterest());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return AE_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_AE";
    }

    @Override
    public String getTablePrefix() {
        return "AE";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        return "select AE_UNQ_SHA1, AE_SEC_HASH, AE_ID "
                + "from RESULT_AE "
                + "inner join RESULT_PATIENT on PAT_ID = AE_PAT_ID "
                + "where PAT_STD_ID = ?";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT AE_PAT_ID FROM "
                + "RESULT_AE e "
                + "LEFT JOIN RESULT_PATIENT p ON (e.AE_PAT_ID = p.PAT_ID) "
                + "LEFT JOIN RESULT_STUDY s ON (p.PAT_STD_ID = s.STD_ID) "
                + "WHERE s.STD_NAME = ?", String.class, studyName);

    }
}

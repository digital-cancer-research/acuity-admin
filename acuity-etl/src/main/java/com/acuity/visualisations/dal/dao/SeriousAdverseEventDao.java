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
import com.acuity.visualisations.model.output.entities.SeriousAdverseEvent;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class SeriousAdverseEventDao extends EntityDao<SeriousAdverseEvent> {
    private static final String RESULT_SERIOUS_ADVERSE_EVENT = "RESULT_SERIOUS_ADVERSE_EVENT";

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_SERIOUS_ADVERSE_EVENT;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("SAE_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_ADVERSE_EVENT").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CRIT_MET_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_INV_AWARE_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_RES_IN_DEATH").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_LIFE_THR").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_REQR_HOSP").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_HOSP_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_DISCH_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_PERS_DISABILITY").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CONG_ANOM").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_OTHER_EVENT").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CAUSED_OTHER_MED").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_OTHER_MED").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CAUSED_STD_PROCD").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_STD_PROCD").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_AE_DESC").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_AE_NUM").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_PRIM_CAUSE_DEATH").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_SEC_CAUSE_DEATH").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_ADD_DRUG").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_ADD_DRUG1").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG1").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_ADD_DRUG2").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG2").build());
        fieldsToInsert.add(fieldBuilder.setField("SAE_PAT_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, SeriousAdverseEvent entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAeText());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAeBecomeSeriousDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAeFindOutDate()));
        ps.setString(paramIndex++, entity.getIsResultInDeath());
        ps.setString(paramIndex++, entity.getIsLifeThreatening());
        ps.setString(paramIndex++, entity.getIsHospitalizationRequired());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitalizationDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDischargeDate()));
        ps.setString(paramIndex++, entity.getIsDisability());
        ps.setString(paramIndex++, entity.getIsCongenitalAnomaly());
        ps.setString(paramIndex++, entity.getIsOtherSeriousEvent());
        ps.setString(paramIndex++, entity.getIsCausedByOther());
        ps.setString(paramIndex++, entity.getOtherMedication());
        ps.setString(paramIndex++, entity.getIsCausedByStudy());
        ps.setString(paramIndex++, entity.getStudyProcedure());
        ps.setString(paramIndex++, entity.getAeDescription());
        ps.setObject(paramIndex++, entity.getAeNum());
        ps.setString(paramIndex++, entity.getPrimaryCauseOfDeath());
        ps.setString(paramIndex++, entity.getSecondaryCauseOfDeath());
        ps.setString(paramIndex++, entity.getAdditionalDrug());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug());
        ps.setString(paramIndex++, entity.getAdditionalDrug1());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug1());
        ps.setString(paramIndex++, entity.getAdditionalDrug2());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug2());
        ps.setObject(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_SERIOUS_ADVERSE_EVENT;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("SAE_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_ADVERSE_EVENT").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_INV_AWARE_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_RES_IN_DEATH").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_LIFE_THR").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_REQR_HOSP").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_HOSP_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_DISCH_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_PERS_DISABILITY").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CONG_ANOM").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_OTHER_EVENT").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CAUSED_OTHER_MED").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_OTHER_MED").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CAUSED_STD_PROCD").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_STD_PROCD").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_AE_DESC").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_AE_NUM").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_PRIM_CAUSE_DEATH").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_SEC_CAUSE_DEATH").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_ADD_DRUG").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_ADD_DRUG1").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG1").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_ADD_DRUG2").build());
        fieldsToSet.add(fieldBuilder.setField("SAE_CAUSED_BY_ADD_DRUG2").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("SAE_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, SeriousAdverseEvent entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAeText());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAeFindOutDate()));
        ps.setString(paramIndex++, entity.getIsResultInDeath());
        ps.setString(paramIndex++, entity.getIsLifeThreatening());
        ps.setString(paramIndex++, entity.getIsHospitalizationRequired());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitalizationDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDischargeDate()));
        ps.setString(paramIndex++, entity.getIsDisability());
        ps.setString(paramIndex++, entity.getIsCongenitalAnomaly());
        ps.setString(paramIndex++, entity.getIsOtherSeriousEvent());
        ps.setString(paramIndex++, entity.getIsCausedByOther());
        ps.setString(paramIndex++, entity.getOtherMedication());
        ps.setString(paramIndex++, entity.getIsCausedByStudy());
        ps.setString(paramIndex++, entity.getStudyProcedure());
        ps.setString(paramIndex++, entity.getAeDescription());
        ps.setObject(paramIndex++, entity.getAeNum());
        ps.setString(paramIndex++, entity.getPrimaryCauseOfDeath());
        ps.setString(paramIndex++, entity.getSecondaryCauseOfDeath());
        ps.setString(paramIndex++, entity.getAdditionalDrug());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug());
        ps.setString(paramIndex++, entity.getAdditionalDrug1());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug1());
        ps.setString(paramIndex++, entity.getAdditionalDrug2());
        ps.setString(paramIndex++, entity.getCausedByAdditionalDrug2());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "SAE_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_SERIOUS_ADVERSE_EVENT;
    }

    @Override
    public String getTablePrefix() {
        return "SAE";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_SERIOUS_ADVERSE_EVENT).setTargetEntity("RESULT_PATIENT").addColumnToJoin("SAE_PAT_ID", "PAT_ID")
                .build());

        List<TableField> fieldValues = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("SAE_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_SERIOUS_ADVERSE_EVENT, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT SAE_PAT_ID FROM RESULT_SERIOUS_ADVERSE_EVENT e "
                + "left join RESULT_PATIENT p on (e.SAE_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

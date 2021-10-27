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
import com.acuity.visualisations.model.output.entities.MedDosDisc;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class MedDosDiscDao extends EntityDao<MedDosDisc> {
    private static final String RESULT_TARGET_MED_DOS_DISC = "RESULT_TARGET_MED_DOS_DISC";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_TARGET_MED_DOS_DISC;
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("DSC_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_IPDC_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_IPDC_REAS").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_IPDC_SPEC").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_DRUG_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_SUBJ_DEC_SPEC").build());
        fieldsToInsert.add(fieldBuilder.setField("DSC_SUBJ_DEC_SPEC_OTHER").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_TARGET_MED_DOS_DISC;
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField("DSC_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("DSC_IPDC_REAS").build());
        fieldsToSet.add(fieldBuilder.setField("DSC_IPDC_SPEC").build());
        fieldsToSet.add(fieldBuilder.setField("DSC_SUBJ_DEC_SPEC").build());
        fieldsToSet.add(fieldBuilder.setField("DSC_SUBJ_DEC_SPEC_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("DSC_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, MedDosDisc entity) throws SQLException {
        int parameter = 1;
        ps.setObject(parameter++, entity.getId());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getIpdcDate()));
        ps.setObject(parameter++, entity.getIpdcReas());
        ps.setString(parameter++, entity.getIpdcSpec());
        ps.setString(parameter++, entity.getDrugName());
        ps.setString(parameter++, entity.getSha1ForUniqueFields());
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setString(parameter++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(parameter++, entity.getPatientGuid());
        ps.setString(parameter++, entity.getSubjectDecisionSpec());
        ps.setString(parameter, entity.getSubjectDecisionSpecOther());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, MedDosDisc entity) throws SQLException {
        int parameter = 1;
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(parameter++, entity.getIpdcReas());
        ps.setString(parameter++, entity.getIpdcSpec());
        ps.setString(parameter++, entity.getSubjectDecisionSpec());
        ps.setString(parameter++, entity.getSubjectDecisionSpecOther());
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "DSC_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_TARGET_MED_DOS_DISC;
    }

    @Override
    public String getTablePrefix() {
        return "DSC";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_TARGET_MED_DOS_DISC).setTargetEntity("RESULT_PATIENT").addColumnToJoin("DSC_PAT_ID", "PAT_ID")
                .build());
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add("DSC_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_TARGET_MED_DOS_DISC, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT DSC_PAT_ID FROM RESULT_TARGET_MED_DOS_DISC e "
                + "left join RESULT_PATIENT p on (e.DSC_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

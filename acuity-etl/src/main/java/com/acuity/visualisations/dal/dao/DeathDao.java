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
import com.acuity.visualisations.model.output.entities.Death;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class DeathDao extends EntityDao<Death> {
    private static final String RESULT_DEATH = "RESULT_DEATH";
    private static final String DTH_ID = "DTH_ID";

    @Override
    protected String getIdColumnName() {
        return DTH_ID;
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_DEATH;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField(DTH_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_CAUSE").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_DESIGNATION_OF_CAUSE").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_AUTOPSY_PERFORMED").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_RELATED_INV_DISEASE").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_NARRATIVE_CAUSE").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_PREFERRED_TERM").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_LLT").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_HLT").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_SOC").build());
        fieldsToInsert.add(fieldBuilder.setField("DTH_PAT_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_DEATH;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("DTH_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_CAUSE").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_AUTOPSY_PERFORMED").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_RELATED_INV_DISEASE").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_NARRATIVE_CAUSE").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_PREFERRED_TERM").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_LLT").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_HLT").build());
        fieldsToSet.add(fieldBuilder.setField("DTH_SOC").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(DTH_ID).build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Death entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDate()));
        ps.setString(paramIndex++, entity.getCause());
        ps.setString(paramIndex++, entity.getDesignationOfCause());
        ps.setString(paramIndex++, entity.getAutopsyPerformed());
        ps.setString(paramIndex++, entity.getRelatedToInvestigationDisease());
        ps.setString(paramIndex++, entity.getNarrativeCause());
        ps.setString(paramIndex++, entity.getPreferredTerm());
        ps.setString(paramIndex++, entity.getLlt());
        ps.setString(paramIndex++, entity.getHlt());
        ps.setString(paramIndex++, entity.getSoc());
        ps.setString(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Death entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDate()));
        ps.setString(paramIndex++, entity.getCause());
        ps.setString(paramIndex++, entity.getAutopsyPerformed());
        ps.setString(paramIndex++, entity.getRelatedToInvestigationDisease());
        ps.setString(paramIndex++, entity.getNarrativeCause());
        ps.setString(paramIndex++, entity.getPreferredTerm());
        ps.setString(paramIndex++, entity.getLlt());
        ps.setString(paramIndex++, entity.getHlt());
        ps.setString(paramIndex++, entity.getSoc());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    public String getTableName() {
        return RESULT_DEATH;
    }

    @Override
    public String getTablePrefix() {
        return "DTH";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_DEATH).setTargetEntity("RESULT_PATIENT").addColumnToJoin("DTH_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("DTH_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_DEATH, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT DTH_PAT_ID FROM "
                + "RESULT_DEATH e "
                + "left join RESULT_PATIENT p on (e.DTH_PAT_ID = p.PAT_ID) "
                + "LEFT JOIN RESULT_STUDY s ON (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);

    }
}

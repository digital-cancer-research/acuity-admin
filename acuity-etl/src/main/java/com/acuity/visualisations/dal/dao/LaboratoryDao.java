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
import com.acuity.visualisations.model.output.entities.Laboratory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class LaboratoryDao extends EntityDao<Laboratory> {
    private static final String RESULT_LABORATORY = "RESULT_LABORATORY";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_LABORATORY;
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("LAB_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_REF_HIGH").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_REF_LOW").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_COMMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_VALUE_DIPSTICK").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_SCH_TIMEPOINT").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_LGR_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_TST_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_SRC_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LAB_SRC_TYPE").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_LABORATORY;
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("LAB_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_CODE").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_VALUE").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_REF_HIGH").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_REF_LOW").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_COMMENT").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_VALUE_DIPSTICK").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_SCH_TIMEPOINT").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_LGR_ID").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_SRC_ID").build());
        fieldsToSet.add(fieldBuilder.setField("LAB_SRC_TYPE").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("LAB_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Laboratory entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getLabCode());
        ps.setString(paramIndex++, entity.getLaboratoryUnit());
        ps.setBigDecimal(paramIndex++, entity.getLaboratoryValue());
        ps.setBigDecimal(paramIndex++, entity.getRefHigh());
        ps.setBigDecimal(paramIndex++, entity.getRefLow());
        ps.setString(paramIndex++, entity.getComment());
        ps.setString(paramIndex++, entity.getValueDipstick());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimepoint());
        ps.setObject(paramIndex++, entity.getLaboratoryGroupGuid());
        ps.setObject(paramIndex++, entity.getTestGuid());
        ps.setObject(paramIndex++, entity.getSourceGiud());
        ps.setObject(paramIndex, entity.getSourceType());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Laboratory entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getLabCode());
        ps.setString(paramIndex++, entity.getLaboratoryUnit());
        ps.setBigDecimal(paramIndex++, entity.getLaboratoryValue());
        ps.setBigDecimal(paramIndex++, entity.getRefHigh());
        ps.setBigDecimal(paramIndex++, entity.getRefLow());
        ps.setString(paramIndex++, entity.getComment());
        ps.setString(paramIndex++, entity.getValueDipstick());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimepoint());
        ps.setObject(paramIndex++, entity.getLaboratoryGroupGuid());
        ps.setObject(paramIndex++, entity.getSourceGiud());
        ps.setObject(paramIndex++, entity.getSourceType());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "LAB_ID";
    }

    @Override
    public String getTableName() {
        return RESULT_LABORATORY;
    }

    @Override
    public String getTablePrefix() {
        return "LAB";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_LABORATORY).setTargetEntity("RESULT_TEST").addColumnToJoin("LAB_TST_ID", "TST_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_TEST").setTargetEntity("RESULT_PATIENT").addColumnToJoin("TST_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add("LAB_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_LABORATORY, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT TST_PAT_ID FROM RESULT_LABORATORY e "
                + "left join RESULT_TEST t on (t.TST_ID = e.LAB_TST_ID) "
                + "left join RESULT_PATIENT p on (t.TST_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);

    }
}

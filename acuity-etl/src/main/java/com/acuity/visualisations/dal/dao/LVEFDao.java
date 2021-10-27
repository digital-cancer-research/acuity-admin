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
import com.acuity.visualisations.model.output.entities.LVEF;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class LVEFDao extends EntityDao<LVEF> {
    private static final String RESULT_LVEF = "RESULT_LVEF";
    private static final String LVF_ID = "LVF_ID";

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_LVEF;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField(LVF_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_LVEF").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_METHOD").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_METHOD_OTHER").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("LVF_TST_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_LVEF;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("LVF_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("LVF_LVEF").build());
        fieldsToSet.add(fieldBuilder.setField("LVF_METHOD").build());
        fieldsToSet.add(fieldBuilder.setField("LVF_METHOD_OTHER").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(LVF_ID).build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, LVEF entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex++, entity.getLvef());
        ps.setString(paramIndex++, entity.getMethod());
        ps.setString(paramIndex++, entity.getMethodOther());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex, entity.getTestGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, LVEF entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex++, entity.getLvef());
        ps.setString(paramIndex++, entity.getMethod());
        ps.setString(paramIndex++, entity.getMethodOther());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return LVF_ID;
    }

    @Override
    public String getTableName() {
        return RESULT_LVEF;
    }

    @Override
    public String getTablePrefix() {
        return "LVF";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_LVEF).setTargetEntity("RESULT_TEST").addColumnToJoin("LVF_TST_ID", "TST_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_TEST").setTargetEntity("RESULT_PATIENT").addColumnToJoin("TST_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("LVF_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_LVEF, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT TST_PAT_ID FROM RESULT_LVEF e "
                + "left join RESULT_TEST t on (t.TST_ID = e.LVF_TST_ID) "
                + "left join RESULT_PATIENT p on (t.TST_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);

    }
}

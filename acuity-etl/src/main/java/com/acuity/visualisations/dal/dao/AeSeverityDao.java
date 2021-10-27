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
import com.acuity.visualisations.model.output.entities.AeSeverity;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class AeSeverityDao extends EntityDao<AeSeverity> {
    private static final String AES_ID = "AES_ID";

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());

        fieldsToInsert.add(fieldBuilder.setField(AES_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("AES_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("AES_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AES_DATE_UPDATED").build());

        fieldsToInsert.add(fieldBuilder.setField("AES_START_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("AES_END_DATE_RAW").build());
        fieldsToInsert.add(fieldBuilder.setField("AES_END_DATE").build());

        fieldsToInsert.add(fieldBuilder.setField("AES_SEVERITY").build());
        fieldsToInsert.add(fieldBuilder.setField("AES_AE_ID").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("AES_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("AES_END_DATE_RAW").build());
        fieldsToSet.add(fieldBuilder.setField("AES_END_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("AES_SEVERITY").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(AES_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, AeSeverity entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));

        ps.setString(paramIndex++, entity.getSeverity());

        ps.setString(paramIndex, entity.getAeGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, AeSeverity entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEndDate()));
        ps.setString(paramIndex++, entity.getSeverity());
        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return AES_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_AE_SEVERITY";
    }

    @Override
    public String getTablePrefix() {
        return "AES";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        return "select AES_UNQ_SHA1, AES_SEC_HASH, AES_ID "
                + "from RESULT_AE_SEVERITY "
                + "inner join RESULT_AE on AE_ID = AES_AE_ID "
                + "inner join RESULT_PATIENT on PAT_ID = AE_PAT_ID "
                + "where PAT_STD_ID = ?";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

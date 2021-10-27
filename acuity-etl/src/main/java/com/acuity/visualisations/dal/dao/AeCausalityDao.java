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
import com.acuity.visualisations.model.output.entities.AeCausality;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class AeCausalityDao extends EntityDao<AeCausality> {
    private static final String AEC_ID = "AEC_ID";

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());

        fieldsToInsert.add(fieldBuilder.setField(AEC_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("AEC_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("AEC_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("AEC_DATE_UPDATED").build());

        fieldsToInsert.add(fieldBuilder.setField("AEC_CAUSALITY").build());

        fieldsToInsert.add(fieldBuilder.setField("AEC_AE_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("AEC_DRUG_ID").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("AEC_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("AEC_CAUSALITY").build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(AEC_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, AeCausality entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getCausality());
        ps.setString(paramIndex++, entity.getAeGuid());
        ps.setString(paramIndex, entity.getDrugGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, AeCausality entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getCausality());
        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return AEC_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_AE_CAUSALITY";
    }

    @Override
    public String getTablePrefix() {
        return "AEC";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        return "select AEC_UNQ_SHA1, AEC_SEC_HASH, AEC_ID "
                + "from RESULT_AE_CAUSALITY "
                + "inner join RESULT_AE on AE_ID = AEC_AE_ID "
                + "inner join RESULT_PATIENT on PAT_ID = AE_PAT_ID "
                + "where PAT_STD_ID = ?";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

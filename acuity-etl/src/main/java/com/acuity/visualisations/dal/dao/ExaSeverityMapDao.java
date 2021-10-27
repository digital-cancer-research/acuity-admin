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
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.ExaSeverityMap;
import java.util.Collections;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class ExaSeverityMapDao extends EntityDao<ExaSeverityMap> {
    private static final String RESULT_EXA_SEVERITY_MAP = "RESULT_EXA_SEVERITY_MAP";
    private static final String ESM_ID = "ESM_ID";

    @Override
    protected String getInsertStatement() {
        String targetTable = RESULT_EXA_SEVERITY_MAP;
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField(ESM_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_DATE_UPDATED").build());
        
        fieldsToInsert.add(fieldBuilder.setField("ESM_SEVERITY").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_DEPOT_GCS").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_SYSCORT_TRT").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_ICS_TRT").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_ANTIBIOTICS_TRT").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_HOSPIT").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_EMER_TRT").build());
        
        fieldsToInsert.add(fieldBuilder.setField("ESM_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("ESM_STD_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = RESULT_EXA_SEVERITY_MAP;
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField("ESM_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField(ESM_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, ExaSeverityMap entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getExcClass());
        ps.setString(paramIndex++, entity.getDepotGcs());
        ps.setString(paramIndex++, entity.getSyscortTrt());
        ps.setString(paramIndex++, entity.getIcsTrt());
        ps.setString(paramIndex++, entity.getAntibioticsTrt());
        ps.setString(paramIndex++, entity.getHospit());
        ps.setString(paramIndex++, entity.getEmerTrt());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setString(paramIndex, entity.getStudyGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, ExaSeverityMap entity) throws SQLException {
        int parameter = 1;
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return ESM_ID;
    }

    @Override
    public String getTableName() {
        return RESULT_EXA_SEVERITY_MAP;
    }

    @Override
    public String getTablePrefix() {
        return "ESM";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField(RESULT_EXA_SEVERITY_MAP, "ESM_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add("ESM_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_EXA_SEVERITY_MAP, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return Collections.emptyList();
    }
}

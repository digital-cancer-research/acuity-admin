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
import com.acuity.visualisations.model.output.entities.LungFunction;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

/**
 * Created by knml167 on 30/07/2015.
 */
@Repository
public class LungFunctionDao extends EntityDao<LungFunction> {
    @Override
    protected String getInsertStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("LNG_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("LNG_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_VISIT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_ASSESS_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_PROT_SCHEDULE").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_MEASUREMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_RESULT").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_DLCO").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_TOT_LUNG_CAP").build());
        fieldsToInsert.add(fieldBuilder.setField("LNG_INSP_OX_FRAC").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, LungFunction entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setObject(paramIndex++, entity.getPatientGuid());
        ps.setBigDecimal(paramIndex++, entity.getVisit());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAssessDate()));
        ps.setString(paramIndex++, entity.getProtocolSchedule());
        ps.setString(paramIndex++, entity.getTestName());
        ps.setBigDecimal(paramIndex++, entity.getTestResult());
        ps.setBigDecimal(paramIndex++, entity.getDiffusingCapacity());
        ps.setBigDecimal(paramIndex++, entity.getTotalLungCapacity());
        ps.setBigDecimal(paramIndex, entity.getInspiredOxFraction());
    }

    @Override
    protected String getIdColumnName() {
        return "LNG_ID";
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("LNG_DATE_UPDATED").build());

        fieldsToSet.add(fieldBuilder.setField("LNG_VISIT").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_VISIT_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_PROT_SCHEDULE").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_MEASUREMENT").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_RESULT").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_DLCO").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_TOT_LUNG_CAP").build());
        fieldsToSet.add(fieldBuilder.setField("LNG_INSP_OX_FRAC").build());

        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("LNG_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, LungFunction entity) throws SQLException {
        int parameter = 1;
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setBigDecimal(parameter++, entity.getVisit());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getVisitDate()));

        ps.setString(parameter++, entity.getProtocolSchedule());

        ps.setString(parameter++, entity.getTestName());
        ps.setBigDecimal(parameter++, entity.getTestResult());
        ps.setBigDecimal(parameter++, entity.getDiffusingCapacity());
        ps.setBigDecimal(parameter++, entity.getTotalLungCapacity());
        ps.setBigDecimal(parameter++, entity.getInspiredOxFraction());

        ps.setObject(parameter, entity.getId());
    }

    @Override
    public String getTableName() {
        return "RESULT_LUNGFUNC";
    }

    @Override
    protected String getTablePrefix() {
        return "LNG";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(getTableName()).setTargetEntity("RESULT_PATIENT").addColumnToJoin("LNG_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("LNG_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(getTableName(), hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT LNG_PAT_ID FROM RESULT_LUNGFUNC l "
                + "left join RESULT_PATIENT p on (l.LNG_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

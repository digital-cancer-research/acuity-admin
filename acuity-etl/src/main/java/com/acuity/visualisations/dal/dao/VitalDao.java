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
import com.acuity.visualisations.model.output.entities.VitalThin;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class VitalDao extends EntityDao<VitalThin> {

    @Override
    protected String getInsertStatement() {
        String targetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("VIT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_DATE_UPDATED").build());

        fieldsToInsert.add(fieldBuilder.setField("VIT_LPDAT").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_LPDOS").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_ENTITY_CLASS").build());

        fieldsToInsert.add(fieldBuilder.setField("VIT_TEST_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_ANATOMICAL_LOCATION").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_PHYSICAL_POSITION").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_ANATOMICAL_SIDE_INTEREST").build());

        fieldsToInsert.add(fieldBuilder.setField("VIT_CLINICALLY_SIGNIFICANT").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_SCH_TIMEPOINT").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_LAST_IP_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("VIT_LAST_IP_DOSE").build());

        fieldsToInsert.add(fieldBuilder.setField("VIT_TST_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, VitalThin entity) throws SQLException {
        int parameter = 1;
        ps.setString(parameter++, entity.getId());
        ps.setString(parameter++, entity.getSha1ForUniqueFields());
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setString(parameter++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getLpdat()));
        ps.setBigDecimal(parameter++, entity.getLpdos());
        ps.setBigDecimal(parameter++, entity.getTestResult());
        ps.setString(parameter++, entity.getClass().getSimpleName());

        ps.setString(parameter++, entity.getTestName());
        ps.setString(parameter++, entity.getResultUnit());
        ps.setString(parameter++, entity.getAnatomicalLocation());
        ps.setString(parameter++, entity.getPhysicalPosition());
        ps.setString(parameter++, entity.getAnatomicalSideOfInterest());

        ps.setString(parameter++, entity.getClinicallySignificant());
        ps.setString(parameter++, entity.getProtocolScheduleTimepoint());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getLastDoseDate()));
        ps.setString(parameter++, entity.getLastDoseAmount());

        ps.setObject(parameter, entity.getTestGuid());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("VIT_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_LPDAT").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_LPDOS").build());

        fieldsToSet.add(fieldBuilder.setField("VIT_VALUE").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_ENTITY_CLASS").build());

        fieldsToSet.add(fieldBuilder.setField("VIT_CLINICALLY_SIGNIFICANT").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_SCH_TIMEPOINT").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_LAST_IP_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("VIT_LAST_IP_DOSE").build());

        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("VIT_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, VitalThin entity) throws SQLException {
        int parameter = 1;
        ps.setObject(parameter++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getLpdat()));
        ps.setBigDecimal(parameter++, entity.getLpdos());

        ps.setBigDecimal(parameter++, entity.getTestResult());
        ps.setString(parameter++, entity.getClass().getSimpleName());

        ps.setString(parameter++, entity.getClinicallySignificant());
        ps.setString(parameter++, entity.getProtocolScheduleTimepoint());
        ps.setTimestamp(parameter++, getSQLTimestamp(entity.getLastDoseDate()));
        ps.setString(parameter++, entity.getLastDoseAmount());

        ps.setObject(parameter, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "VIT_ID";
    }

    @Override
    public String getTableName() {
        return "RESULT_VITALS";
    }

    @Override
    public String getTablePrefix() {
        return "VIT";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(getTableName()).setTargetEntity("RESULT_TEST").addColumnToJoin("VIT_TST_ID", "TST_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_TEST").setTargetEntity("RESULT_PATIENT").addColumnToJoin("TST_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("VIT_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(getTableName(), hashColumnNames, joinChain, fieldValues)
                + " AND VIT_ENTITY_CLASS = '" + entityClass.getSimpleName() + "'";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT TST_PAT_ID FROM RESULT_VITALS e "
                + "left join RESULT_TEST t on (t.TST_ID = e.VIT_TST_ID) "
                + "left join RESULT_PATIENT p on (t.TST_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

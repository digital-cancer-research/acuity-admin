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
import com.acuity.visualisations.model.output.entities.StackedPkResults;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class StackedPkResultsDao extends EntityDao<StackedPkResults> {
    @Override
    protected String getInsertStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("STP_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("STP_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("STP_VISIT_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_VISIT_NUMBER").build());

        fieldsToInsert.add(fieldBuilder.setField("STP_TREATMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_TREATMENT_SCHEDULE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_TREATMENT_CYCLE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PARAMETER").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_ANALYTE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PARAMETER_VALUE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PARAMETER_VALUE_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHEDULE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_START_DAY").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_START_HOUR").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_START_MINUTE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHEDULE_END").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_END_DAY").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_END_HOUR").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_PROTOCOL_SCHD_END_MINUTE").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_COMMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("STP_ACTUAL_DOSE").build());

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, StackedPkResults entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setBigDecimal(paramIndex++, entity.getVisitNumber());

        ps.setString(paramIndex++, entity.getTreatment());
        ps.setString(paramIndex++, entity.getTreatmentSchedule());
        ps.setString(paramIndex++, entity.getTreatmentCycle());
        ps.setString(paramIndex++, entity.getParameter());
        ps.setString(paramIndex++, entity.getAnalyte());
        ps.setBigDecimal(paramIndex++, entity.getParameterValue());
        ps.setString(paramIndex++, entity.getParameterValueUnit());
        ps.setString(paramIndex++, entity.getProtocolSchedule());
        ps.setString(paramIndex++, entity.getProtocolScheduleStartDay());
        ps.setObject(paramIndex++, entity.getProtocolScheduleStartHour());
        ps.setObject(paramIndex++, entity.getProtocolScheduleStartMinute());
        ps.setString(paramIndex++, entity.getProtocolScheduleEnd());
        ps.setString(paramIndex++, entity.getProtocolScheduleEndDay());
        ps.setObject(paramIndex++, entity.getProtocolScheduleEndHour());
        ps.setObject(paramIndex++, entity.getProtocolScheduleEndMinute());
        ps.setString(paramIndex++, entity.getComment());
        ps.setString(paramIndex++, entity.getVisit());
        ps.setString(paramIndex, entity.getActualDose());
    }

    @Override
    protected String getIdColumnName() {
        return "STP_ID";
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("STP_DATE_UPDATED").build());

        fieldsToSet.add(fieldBuilder.setField("STP_PARAMETER_VALUE").build());
        fieldsToSet.add(fieldBuilder.setField("STP_PARAMETER_VALUE_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("STP_COMMENT").build());

        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("STP_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, StackedPkResults entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setBigDecimal(paramIndex++, entity.getParameterValue());
        ps.setString(paramIndex++, entity.getParameterValueUnit());
        ps.setString(paramIndex++, entity.getComment());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    public String getTableName() {
        return "RESULT_STACKED_PK_RESULTS";
    }

    @Override
    protected String getTablePrefix() {
        return "STP";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity("RESULT_STACKED_PK_RESULTS").setTargetEntity("RESULT_PATIENT").addColumnToJoin("STP_PAT_ID", "PAT_ID").build());

        List<TableField> fieldValues = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("STP_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery("RESULT_STACKED_PK_RESULTS", hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT STP_PAT_ID FROM RESULT_STACKED_PK_RESULTS "
                + "left join RESULT_PATIENT on (STP_PAT_ID = PAT_ID) "
                + "left join RESULT_STUDY on (PAT_STD_ID = STD_ID) "
                + "where STD_NAME = ?", String.class, studyName);
    }

}

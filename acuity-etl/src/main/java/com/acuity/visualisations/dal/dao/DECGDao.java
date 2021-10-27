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
import com.acuity.visualisations.model.output.entities.DECG;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class DECGDao extends EntityDao<DECG> {
    private static final String RESULT_DECG = "RESULT_DECG";
    private static final String DECG_ID = "DECG_ID";

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList(
                "SELECT DISTINCT TST_PAT_ID FROM RESULT_DECG e " + "left join RESULT_TEST t on (t.TST_ID = e.DECG_TST_ID) "
                        + "left join RESULT_PATIENT p on (t.TST_PAT_ID = p.PAT_ID) " + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                        + "where s.STD_NAME = ?", String.class, studyName);
    }

    @Override
    public String getTableName() {
        return RESULT_DECG;
    }

    @Override
    protected String getTablePrefix() {
        return "DECG";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity(RESULT_DECG).setTargetEntity("RESULT_TEST").addColumnToJoin("DECG_TST_ID", "TST_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_TEST").setTargetEntity("RESULT_PATIENT").addColumnToJoin("TST_PAT_ID", "PAT_ID").build());

        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("DECG_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        String sql = QueryBuilderUtil.buildSelectHashesQuery(RESULT_DECG, hashColumnNames, joinChain, fieldValues);
        return sql;
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = RESULT_DECG;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField(DECG_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_ABNORMALITY").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_EVALUATION").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_SIGNIFICANT").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_SCH_TIMEPOINT").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_MEASURMENT_LABEL").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_MEASURMENT_VALUE").build());

        fieldsToInsert.add(fieldBuilder.setField("DECG_METHOD").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_BEAT_GROUP_NUM").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_BEAT_NUM_IN_BEAT_GROUP").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_NUM_BEATS_AVR_BEAT").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_BEAT_GROUP_LENGTH_SEC").build());
        fieldsToInsert.add(fieldBuilder.setField("DECG_COMMENT").build());

        fieldsToInsert.add(fieldBuilder.setField("DECG_TST_ID").build());
        String sql = QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, DECG entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAbnormality());
        ps.setString(paramIndex++, entity.getEvaluation());
        ps.setString(paramIndex++, entity.getSignificant());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimepoint());
        ps.setString(paramIndex++, entity.getMeasurementLabel());
        ps.setObject(paramIndex++, entity.getMeasurementValue());

        ps.setString(paramIndex++, entity.getMethod());
        ps.setObject(paramIndex++, entity.getBeatGroupNumber());
        ps.setObject(paramIndex++, entity.getBeatNumberWithinBeatGroup());
        ps.setObject(paramIndex++, entity.getNumberOfBeatsInAverageBeat());
        ps.setObject(paramIndex++, entity.getBeatGroupLengthInSec());
        ps.setString(paramIndex++, entity.getComment());

        ps.setObject(paramIndex, entity.getTestGuid());
    }

    @Override
    protected String getIdColumnName() {
        return DECG_ID;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, DECG entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getAbnormality());
        ps.setString(paramIndex++, entity.getEvaluation());
        ps.setString(paramIndex++, entity.getSignificant());
        ps.setString(paramIndex++, entity.getProtocolScheduleTimepoint());
        ps.setString(paramIndex++, entity.getMeasurementValue());

        ps.setString(paramIndex++, entity.getMethod());
        ps.setObject(paramIndex++, entity.getBeatGroupNumber());
        ps.setObject(paramIndex++, entity.getBeatNumberWithinBeatGroup());
        ps.setObject(paramIndex++, entity.getNumberOfBeatsInAverageBeat());
        ps.setObject(paramIndex++, entity.getBeatGroupLengthInSec());
        ps.setString(paramIndex++, entity.getComment());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = RESULT_DECG;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("DECG_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_ABNORMALITY").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_EVALUATION").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_SIGNIFICANT").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_SCH_TIMEPOINT").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_MEASURMENT_VALUE").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_METHOD").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_BEAT_GROUP_NUM").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_BEAT_NUM_IN_BEAT_GROUP").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_NUM_BEATS_AVR_BEAT").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_BEAT_GROUP_LENGTH_SEC").build());
        fieldsToSet.add(fieldBuilder.setField("DECG_COMMENT").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(DECG_ID).build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

}

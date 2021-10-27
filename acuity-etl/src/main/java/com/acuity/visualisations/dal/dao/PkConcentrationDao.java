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
import com.acuity.visualisations.model.output.entities.PkConcentration;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PkConcentrationDao extends EntityDao<PkConcentration> {
    @Override
    protected String getInsertStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToInsert.add(fieldBuilder.setField("PKC_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_PAT_ID").build());

        fieldsToInsert.add(fieldBuilder.setField("PKC_SPECIMEN_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_ANALYTE").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_ANALYTE_CONCENTRATION").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_ANALYTE_CONCENTRATION_UNIT").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_LOWER_LIMIT").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_TREATMENT_CYCLE").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_TREATMENT").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_TREATMENT_SCHEDULE").build());
        fieldsToInsert.add(fieldBuilder.setField("PKC_COMMENT").build());

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, PkConcentration entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setObject(paramIndex++, entity.getPatientGuid());

        ps.setString(paramIndex++, entity.getSpcIdentifier());
        ps.setString(paramIndex++, entity.getAnalyte());
        ps.setObject(paramIndex++, entity.getAnalyteConcentration());
        ps.setString(paramIndex++, entity.getAnalyteConcentrationUnit());
        ps.setBigDecimal(paramIndex++, entity.getLowerLimit());
        ps.setString(paramIndex++, entity.getTreatmentCycle());
        ps.setString(paramIndex++, entity.getTreatment());
        ps.setString(paramIndex++, entity.getTreatmentSchedule());
        ps.setString(paramIndex, entity.getComment());
    }

    @Override
    protected String getIdColumnName() {
        return "PKC_ID";
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        List<TableField> fieldsToSet = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("PKC_DATE_UPDATED").build());

        fieldsToSet.add(fieldBuilder.setField("PKC_SPECIMEN_ID").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_ANALYTE").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_ANALYTE_CONCENTRATION").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_ANALYTE_CONCENTRATION_UNIT").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_LOWER_LIMIT").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_TREATMENT_CYCLE").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_TREATMENT").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_TREATMENT_SCHEDULE").build());
        fieldsToSet.add(fieldBuilder.setField("PKC_COMMENT").build());

        List<TableField> whereFields = new ArrayList<>();
        whereFields.add(fieldBuilder.setField("PKC_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, PkConcentration entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getSpcIdentifier());
        ps.setString(paramIndex++, entity.getAnalyte());
        ps.setObject(paramIndex++, entity.getAnalyteConcentration());
        ps.setString(paramIndex++, entity.getAnalyteConcentrationUnit());
        ps.setBigDecimal(paramIndex++, entity.getLowerLimit());
        ps.setString(paramIndex++, entity.getTreatmentCycle());
        ps.setString(paramIndex++, entity.getTreatment());
        ps.setString(paramIndex++, entity.getTreatmentSchedule());
        ps.setString(paramIndex++, entity.getComment());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    public String getTableName() {
        return "RESULT_PK_CONCENTRATION";
    }

    @Override
    protected String getTablePrefix() {
        return "PKC";
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();

        joinChain.add(builder.setSourceEntity("RESULT_PK_CONCENTRATION").setTargetEntity("RESULT_PATIENT").addColumnToJoin("PKC_PAT_ID", "PAT_ID").build());

        List<TableField> fieldValues = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder("RESULT_PATIENT");
        fieldValues.add(fieldBuilder.setField("PAT_STD_ID").build());
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("PKC_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery("RESULT_PK_CONCENTRATION", hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT PKC_PAT_ID FROM RESULT_PK_CONCENTRATION "
                + "left join RESULT_PATIENT on (PKC_PAT_ID = PAT_ID) "
                + "left join RESULT_STUDY on (PAT_STD_ID = STD_ID) "
                + "where STD_NAME = ?", String.class, studyName);
    }
}

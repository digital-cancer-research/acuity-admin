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
import com.acuity.visualisations.model.output.entities.PrimaryTumourLocation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PrimaryTumourLocationDao extends EntityDao<PrimaryTumourLocation> {
    private static final String RESULT_PRIMARY_TUMOUR_LOCATION = "RESULT_PRIMARY_TUMOUR_LOCATION";

    @Override
    protected String getIdColumnName() {
        return "PTL_ID";
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, PrimaryTumourLocation entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setObject(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setString(paramIndex++, entity.getPatientGuid());
        ps.setString(paramIndex++, entity.getPrimaryTumLocation());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getOriginalDiagnosisDate()));
        ps.setString(paramIndex, entity.getPrimaryTumLocationComment());
    }

    protected void prepareStatementToUpdate(PreparedStatement ps, PrimaryTumourLocation tumourLocation) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, tumourLocation.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(tumourLocation.getDateUpdated()));
        ps.setString(paramIndex++, tumourLocation.getPrimaryTumLocation());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(tumourLocation.getOriginalDiagnosisDate()));
        ps.setString(paramIndex++, tumourLocation.getPrimaryTumLocationComment());
        ps.setString(paramIndex, tumourLocation.getId());
    }

    protected String getInsertStatement() {
        String targetTable = RESULT_PRIMARY_TUMOUR_LOCATION;
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("PTL_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_PAT_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_PRIM_TUM_LOCATION").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_ORIGINAL_DIAGNOSIS_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("PTL_PRIM_TUM_LOCATION_COMMENT").build());
        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    protected String getUpdateStatement() {
        String tagetTable = RESULT_PRIMARY_TUMOUR_LOCATION;
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToSet.add(fieldBuilder.setField("PTL_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("PTL_PRIM_TUM_LOCATION").build());
        fieldsToSet.add(fieldBuilder.setField("PTL_ORIGINAL_DIAGNOSIS_DATE").build());
        fieldsToSet.add(fieldBuilder.setField("PTL_PRIM_TUM_LOCATION_COMMENT").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("PTL_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    public String getTableName() {
        return RESULT_PRIMARY_TUMOUR_LOCATION;
    }

    @Override
    public String getTablePrefix() {
        return "PTL";
    }

    @Override
    public String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(RESULT_PRIMARY_TUMOUR_LOCATION).setTargetEntity("RESULT_PATIENT").addColumnToJoin("PTL_PAT_ID", "PAT_ID")
                .build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("PTL_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(RESULT_PRIMARY_TUMOUR_LOCATION, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT PTL_PAT_ID FROM RESULT_PRIMARY_TUMOUR_LOCATION e "
                + "left join RESULT_PATIENT p on (e.PTL_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

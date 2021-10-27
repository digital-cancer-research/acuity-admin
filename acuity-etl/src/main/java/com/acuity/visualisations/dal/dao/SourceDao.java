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
import com.acuity.visualisations.model.output.entities.Source;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class SourceDao extends EntityDao<Source> {

    private static final String TABLE = "RESULT_SOURCE";
    private static final String PREFIX = "src";
    private static final String ID = PREFIX + "_id";

    @Override
    protected String getInsertStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(TABLE);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("src_id").build(),
                fieldBuilder.setField("src_date_created").build(),
                fieldBuilder.setField("src_date_updated").build(),
                fieldBuilder.setField("src_unq_sha1").build(),
                fieldBuilder.setField(getSecondaryHashColumnName()).build(),
                fieldBuilder.setField("src_ref_sha1").build(),

                fieldBuilder.setField("SRC_NAME").build(),
                fieldBuilder.setField("SRC_VERSION").build(),
                fieldBuilder.setField("SRC_TYPE").build()
        );

        return QueryBuilderUtil.buildInsertQuery(TABLE, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Source entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());

        ps.setString(paramIndex++, entity.getName());
        ps.setString(paramIndex++, entity.getVersion());
        ps.setString(paramIndex, entity.getType());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Source entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getName());
        ps.setString(paramIndex++, entity.getVersion());
        ps.setString(paramIndex++, entity.getType());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(TABLE);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField(getSecondaryHashColumnName()).build(),
                fieldBuilder.setField("src_date_updated").build(),

                fieldBuilder.setField("SRC_NAME").build(),
                fieldBuilder.setField("SRC_VERSION").build(),
                fieldBuilder.setField("SRC_TYPE").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(ID).build());
        return QueryBuilderUtil.buildUpdateQuery(TABLE, fieldsToSet, whereFields);
    }

    @Override
    protected String getIdColumnName() {
        return ID;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    protected String getTablePrefix() {
        return PREFIX;
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(TABLE).setTargetEntity("RESULT_LABORATORY").addColumnToJoin(ID, "LAB_SRC_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_LABORATORY").setTargetEntity("RESULT_TEST").addColumnToJoin("LAB_TST_ID", "TST_ID").build());
        joinChain.add(builder.setSourceEntity("RESULT_TEST").setTargetEntity("RESULT_PATIENT").addColumnToJoin("TST_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<>();
        hashColumnNames.add(getUniqueSha1ColumnName());
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(TABLE, hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select distinct pat_id "
                + " from " + TABLE
                + " join RESULT_LABORATORY e on src_id=lab_src_id"
                + " left join RESULT_TEST t on (t.TST_ID = e.LAB_TST_ID) "
                + " left join RESULT_PATIENT p on (t.TST_PAT_ID = p.PAT_ID) "
                + " join result_study ON std_id=pat_std_id "
                + " where std_name=?", String.class, studyName);
    }

    public boolean exist(String sourceId) {
        String query = "SELECT count(*) FROM RESULT_SOURCE WHERE SRC_ID = ?";
        int count = getJdbcTemplate().queryForObject(
                query, new Object[]{sourceId}, Integer.class);
        return count > 0;
    }


}

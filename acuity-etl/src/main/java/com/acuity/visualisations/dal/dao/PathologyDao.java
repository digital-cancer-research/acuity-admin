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
import com.acuity.visualisations.model.output.entities.Pathology;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class PathologyDao extends EntityDao<Pathology> {

    @Override
    public String getTableName() {
        return "result_pathology";
    }

    @Override
    protected String getTablePrefix() {
        return "pth";
    }

    @Override
    protected String getIdColumnName() {
        return "pth_id";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT pat_id "
                + " FROM RESULT_PATHOLOGY "
                + " JOIN result_patient ON pat_id=pth_pat_id "
                + " JOIN result_study ON std_id=pat_std_id "
                + " WHERE std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select pth_unq_sha1, pth_sec_hash, pth_id "
                + " from result_pathology "
                + " join result_patient on pat_id=pth_pat_id "
                + " where pat_std_id=?";
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = getTableName();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("pth_id").build(),
                fieldBuilder.setField("pth_unq_sha1").build(),
                fieldBuilder.setField("pth_sec_hash").build(),
                fieldBuilder.setField("pth_date_created").build(),
                fieldBuilder.setField("pth_date_updated").build(),
                fieldBuilder.setField("pth_pat_id").build(),

                fieldBuilder.setField("pth_date").build(),
                fieldBuilder.setField("pth_his_type").build(),
                fieldBuilder.setField("pth_his_type_details").build(),
                fieldBuilder.setField("pth_tumour_grade").build(),
                fieldBuilder.setField("pth_stage").build(),
                fieldBuilder.setField("pth_tumor_location").build(),
                fieldBuilder.setField("pth_prim_tum_status").build(),
                fieldBuilder.setField("pth_nodes_status").build(),
                fieldBuilder.setField("pth_metastases_status").build(),
                fieldBuilder.setField("pth_determ_method").build(),
                fieldBuilder.setField("pth_other_methods").build()

        );

        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Pathology entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDate()));
        ps.setString(paramIndex++, entity.getHistologyType());
        ps.setString(paramIndex++, entity.getHistologyTypeDetails());
        ps.setString(paramIndex++, entity.getTumourGrade());
        ps.setString(paramIndex++, entity.getStage());
        ps.setString(paramIndex++, entity.getTumorLocation());
        ps.setString(paramIndex++, entity.getPrimaryTumourStatus());
        ps.setString(paramIndex++, entity.getNodesStatus());
        ps.setString(paramIndex++, entity.getMetastasesStatus());
        ps.setString(paramIndex++, entity.getMethodOfDetermination());
        ps.setString(paramIndex, entity.getOtherMethods());
    }


    @Override
    protected String getUpdateStatement() {
        String tagetTable = getTableName();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("pth_sec_hash").build(),
                fieldBuilder.setField("pth_date_updated").build(),

                fieldBuilder.setField("pth_date").build(),
                fieldBuilder.setField("pth_his_type").build(),
                fieldBuilder.setField("pth_his_type_details").build(),
                fieldBuilder.setField("pth_tumour_grade").build(),
                fieldBuilder.setField("pth_stage").build(),
                fieldBuilder.setField("pth_tumor_location").build(),
                fieldBuilder.setField("pth_prim_tum_status").build(),
                fieldBuilder.setField("pth_nodes_status").build(),
                fieldBuilder.setField("pth_metastases_status").build(),
                fieldBuilder.setField("pth_determ_method").build(),
                fieldBuilder.setField("pth_other_methods").build()

        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField("pth_id").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Pathology entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDate()));
        ps.setString(paramIndex++, entity.getHistologyType());
        ps.setString(paramIndex++, entity.getHistologyTypeDetails());
        ps.setString(paramIndex++, entity.getTumourGrade());
        ps.setString(paramIndex++, entity.getStage());
        ps.setString(paramIndex++, entity.getTumorLocation());
        ps.setString(paramIndex++, entity.getPrimaryTumourStatus());
        ps.setString(paramIndex++, entity.getNodesStatus());
        ps.setString(paramIndex++, entity.getMetastasesStatus());
        ps.setString(paramIndex++, entity.getMethodOfDetermination());
        ps.setString(paramIndex++, entity.getOtherMethods());

        ps.setString(paramIndex, entity.getId());
    }
}

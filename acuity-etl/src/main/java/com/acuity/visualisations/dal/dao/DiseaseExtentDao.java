
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
import com.acuity.visualisations.model.output.entities.DiseaseExtent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class DiseaseExtentDao extends EntityDao<DiseaseExtent> {
    private static final String TABLE = "RESULT_DISEASE_EXTENT";
    private static final String PREFIX = "de";
    private static final String ID = PREFIX + "_id";


    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public String getTablePrefix() {
        return PREFIX;
    }

    @Override
    protected String getIdColumnName() {
        return ID;
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select distinct pat_id "
                + " from " + TABLE
                + " join result_patient ON pat_id=" + PREFIX + "_pat_id "
                + " join result_study ON std_id=pat_std_id "
                + " where std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select de_unq_sha1, de_sec_hash, " + ID
                + " from " + TABLE
                + " join result_patient on pat_id=" + PREFIX + "_pat_id "
                + " where pat_std_id=?";
    }


    @Override
    protected String getInsertStatement() {
        String targetTable = TABLE;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField(PREFIX + "_id").build(),
                fieldBuilder.setField(PREFIX + "_unq_sha1").build(),
                fieldBuilder.setField(PREFIX + "_sec_hash").build(),
                fieldBuilder.setField(PREFIX + "_date_created").build(),
                fieldBuilder.setField(PREFIX + "_date_updated").build(),
                fieldBuilder.setField(PREFIX + "_pat_id").build(),

                fieldBuilder.setField("de_meta_loc_adv").build(),
                fieldBuilder.setField("de_site_local_meta_dis").build(),
                fieldBuilder.setField("de_oth_loc_adv_sites").build(),
                fieldBuilder.setField("de_oth_meta_sites").build(),
                fieldBuilder.setField("de_visit_date").build(),
                fieldBuilder.setField("de_rec_prgs_date").build(),
                fieldBuilder.setField("de_recu_earl_cancer").build()


        );

        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }


    @Override
    protected String getUpdateStatement() {
        String targetTable = TABLE;
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField(PREFIX + "_sec_hash").build(),
                fieldBuilder.setField(PREFIX + "_date_updated").build(),

                fieldBuilder.setField("de_meta_loc_adv").build(),
                fieldBuilder.setField("de_site_local_meta_dis").build(),
                fieldBuilder.setField("de_oth_loc_adv_sites").build(),
                fieldBuilder.setField("de_oth_meta_sites").build(),
                fieldBuilder.setField("de_visit_date").build(),
                fieldBuilder.setField("de_rec_prgs_date").build(),
                fieldBuilder.setField("de_recu_earl_cancer").build()

        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(ID).build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToSet, whereFields);
    }


    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, DiseaseExtent entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setString(paramIndex++, entity.getMetastaticLocallyAdvanced());
        ps.setString(paramIndex++, entity.getSiteOfLocalMetastaticDisease());
        ps.setString(paramIndex++, entity.getOtherLocallyAdvancedSites());
        ps.setString(paramIndex++, entity.getOtherMetastaticSites());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRecentProgressionDate()));
        ps.setString(paramIndex, entity.getRecurrenceOfEarlierCancer());

    }


    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, DiseaseExtent entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setString(paramIndex++, entity.getMetastaticLocallyAdvanced());
        ps.setString(paramIndex++, entity.getSiteOfLocalMetastaticDisease());
        ps.setString(paramIndex++, entity.getOtherLocallyAdvancedSites());
        ps.setString(paramIndex++, entity.getOtherMetastaticSites());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getVisitDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getRecentProgressionDate()));
        ps.setString(paramIndex, entity.getRecurrenceOfEarlierCancer());

    }
}

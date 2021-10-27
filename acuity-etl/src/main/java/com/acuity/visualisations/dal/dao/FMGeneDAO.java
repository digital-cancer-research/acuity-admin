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
import com.acuity.visualisations.model.output.entities.FMGene;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class FMGeneDAO extends EntityDao<FMGene> {
    private static final String FM_ID = "FM_ID";

    @Override
    protected String getIdColumnName() {
        return FM_ID;
    }

    @Override
    public String getTableName() {
        return "RESULT_FM_GENE";
    }

    @Override
    protected String getTablePrefix() {
        return "FM";
    }

    @Override
    protected String getInsertStatement() {
        List<TableField> fieldsToInsert = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToInsert.add(fieldBuilder.setField(FM_ID).build());
        fieldsToInsert.add(fieldBuilder.setField("FM_UNQ_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField(getSecondaryHashColumnName()).build());
        fieldsToInsert.add(fieldBuilder.setField("FM_REF_SHA1").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_SAMPLE_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_DISEASE").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_MEDIAN_EXON_COVERAGE").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_KNOWN_VARIANTS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_LIKELY_VARIANTS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_HIGH_LVL_AMPLIFICATIONS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_LOW_LVL_AMPLIFICATIONS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_DELETIONS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_REARRANGEMENTS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_COMMENTS").build());
        fieldsToInsert.add(fieldBuilder.setField("FM_PAT_ID").build());
        return QueryBuilderUtil.buildInsertQuery(getTableName(), fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, FMGene entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSampleId());
        ps.setString(paramIndex++, entity.getDisease());
        ps.setObject(paramIndex++, entity.getMedianExonCoverage());
        ps.setString(paramIndex++, entity.getKnownVariants());
        ps.setString(paramIndex++, entity.getLikelyVariants());
        ps.setString(paramIndex++, entity.getHighLevelAmplifications());
        ps.setString(paramIndex++, entity.getLowLevelAmplifications());
        ps.setString(paramIndex++, entity.getDeletions());
        ps.setString(paramIndex++, entity.getRearrangements());
        ps.setString(paramIndex++, entity.getComments());
        ps.setString(paramIndex, entity.getPatientGuid());
    }

    @Override
    protected String getUpdateStatement() {
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(getTableName());
        fieldsToSet.add(fieldBuilder.setField("FM_DATE_UPDATED").build());
        fieldsToSet.add(fieldBuilder.setField("FM_SAMPLE_ID").build());
        fieldsToSet.add(fieldBuilder.setField("FM_DISEASE").build());
        fieldsToSet.add(fieldBuilder.setField("FM_MEDIAN_EXON_COVERAGE").build());
        fieldsToSet.add(fieldBuilder.setField("FM_KNOWN_VARIANTS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_LIKELY_VARIANTS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_HIGH_LVL_AMPLIFICATIONS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_LOW_LVL_AMPLIFICATIONS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_DELETIONS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_REARRANGEMENTS").build());
        fieldsToSet.add(fieldBuilder.setField("FM_COMMENTS").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField(FM_ID).build());
        return QueryBuilderUtil.buildUpdateQuery(getTableName(), fieldsToSet, whereFields);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, FMGene entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSampleId());
        ps.setString(paramIndex++, entity.getDisease());
        ps.setObject(paramIndex++, entity.getMedianExonCoverage());
        ps.setString(paramIndex++, entity.getKnownVariants());
        ps.setString(paramIndex++, entity.getLikelyVariants());
        ps.setString(paramIndex++, entity.getHighLevelAmplifications());
        ps.setString(paramIndex++, entity.getLowLevelAmplifications());
        ps.setString(paramIndex++, entity.getDeletions());
        ps.setString(paramIndex++, entity.getRearrangements());
        ps.setString(paramIndex++, entity.getComments());
        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        List<JoinDeclaration> joinChain = new ArrayList<JoinDeclaration>();
        JoinDeclarationBuilder builder = new JoinDeclarationBuilder();
        joinChain.add(builder.setSourceEntity(getTableName()).setTargetEntity("RESULT_PATIENT").addColumnToJoin("FM_PAT_ID", "PAT_ID").build());
        List<TableField> fieldValues = new ArrayList<TableField>();
        fieldValues.add(new TableField("RESULT_PATIENT", "PAT_STD_ID"));
        List<String> hashColumnNames = new ArrayList<String>();
        hashColumnNames.add("FM_UNQ_SHA1");
        hashColumnNames.add(getSecondaryHashColumnName());
        hashColumnNames.add(getIdColumnName());
        return QueryBuilderUtil.buildSelectHashesQuery(getTableName(), hashColumnNames, joinChain, fieldValues);
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("SELECT DISTINCT FM_PAT_ID FROM RESULT_FM_GENE fm "
                + "left join RESULT_PATIENT p on (fm.FM_PAT_ID = p.PAT_ID) "
                + "left join RESULT_STUDY s on (p.PAT_STD_ID = s.STD_ID) "
                + "where s.STD_NAME = ?", String.class, studyName);
    }
}

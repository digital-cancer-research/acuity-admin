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
import com.acuity.visualisations.model.output.entities.Biomarker;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;


@Repository
public class BiomarkerDao extends EntityDao<Biomarker> {

    private static final String TABLE = "RESULT_BIOMARKERS";
    private static final String PREFIX = "bmr";
    private static final String ID = PREFIX + "_id";

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList("select distinct pat_id "
                + " from " + TABLE
                + " join result_patient ON pat_id=" + PREFIX + "_pat_id "
                + " join result_study ON std_id=pat_std_id "
                + " where std_name=?", String.class, studyName);
    }

    @Override
    protected String getInsertStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(TABLE);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("bmr_id").build(),
                fieldBuilder.setField("bmr_date_created").build(),
                fieldBuilder.setField("bmr_date_updated").build(),
                fieldBuilder.setField("bmr_unq_sha1").build(),
                fieldBuilder.setField("bmr_sec_hash").build(),
                fieldBuilder.setField("bmr_pat_id").build(),

                fieldBuilder.setField("BMR_GENE").build(),
                fieldBuilder.setField("BMR_SPECIMEN_ID").build(),
                fieldBuilder.setField("BMR_SAMPLE_ID").build(),
                fieldBuilder.setField("BMR_CDNA_CHANGE").build(),
                fieldBuilder.setField("BMR_EXTERNAL_VARIANT_ID").build(),
                fieldBuilder.setField("BMR_TOTAL_READS").build(),
                fieldBuilder.setField("BMR_GERMLINE_FREQUENCY").build(),
                fieldBuilder.setField("BMR_MUTANT_ALLELE_FREQ").build(),
                fieldBuilder.setField("BMR_COPY_NUMBER").build(),
                fieldBuilder.setField("BMR_CIN_RANK").build(),
                fieldBuilder.setField("BMR_MUTATION_TYPE").build(),
                fieldBuilder.setField("BMR_VARIANT_COUNT").build(),
                fieldBuilder.setField("BMR_CHROMOSOME").build(),
                fieldBuilder.setField("BMR_SOMATIC_STATUS").build(),
                fieldBuilder.setField("BMR_AMINO_ACID_CHANGE").build(),
                fieldBuilder.setField("BMR_CHROMOSOME_LOCATION_START").build(),
                fieldBuilder.setField("BMR_CHROMOSOME_LOCATION_END").build(),
                fieldBuilder.setField("BMR_VARIANT_TYPE").build(),
                fieldBuilder.setField("BMR_CNA_TYPE").build(),
                fieldBuilder.setField("BMR_REARR_GENE_1").build(),
                fieldBuilder.setField("BMR_REARR_DESCRIPTION").build(),
                fieldBuilder.setField("BMR_TUMOUR_MUTATIONAL_BURDEN").build()
        );

        return QueryBuilderUtil.buildInsertQuery(TABLE, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Biomarker entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setString(paramIndex++, entity.getGene());
        ps.setString(paramIndex++, entity.getSampleType());
        ps.setString(paramIndex++, entity.getSampleId());
        ps.setString(paramIndex++, entity.getCDNAChange());
        ps.setString(paramIndex++, entity.getExternalVarianId());
        ps.setObject(paramIndex++, entity.getTotalReads());
        ps.setObject(paramIndex++, entity.getGermilineFreq());
        ps.setObject(paramIndex++, entity.getMutantAlleleFreq());
        ps.setObject(paramIndex++, entity.getCopyNumber());
        ps.setObject(paramIndex++, parseToInt(entity.getChromosomeInstabilityNumber()));
        ps.setString(paramIndex++, entity.getMutationType());
        ps.setObject(paramIndex++, entity.getVariantCount());
        ps.setString(paramIndex++, entity.getChromosomeNumber());
        ps.setString(paramIndex++, entity.getSomaticStatus());
        ps.setString(paramIndex++, entity.getAminoAcidChange());
        ps.setObject(paramIndex++, entity.getChromosomeLocationStart());
        ps.setObject(paramIndex++, entity.getChromosomeLocationEnd());
        ps.setString(paramIndex++, entity.getVariantType());
        ps.setString(paramIndex++, entity.getCopyNumberAlterationType());
        ps.setString(paramIndex++, entity.getRearrGene1());
        ps.setString(paramIndex++, entity.getRearrDesc());
        ps.setObject(paramIndex, entity.getTumourMutationBurden());
    }

    @Override
    protected String getIdColumnName() {
        return ID;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Biomarker entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getGene());
        ps.setString(paramIndex++, entity.getSampleType());
        ps.setString(paramIndex++, entity.getSampleId());
        ps.setString(paramIndex++, entity.getCDNAChange());
        ps.setString(paramIndex++, entity.getExternalVarianId());
        ps.setObject(paramIndex++, entity.getTotalReads());
        ps.setObject(paramIndex++, entity.getGermilineFreq());
        ps.setObject(paramIndex++, entity.getMutantAlleleFreq());
        ps.setObject(paramIndex++, entity.getCopyNumber());
        ps.setObject(paramIndex++, entity.getChromosomeInstabilityNumber());
        ps.setString(paramIndex++, entity.getMutationType());
        ps.setObject(paramIndex++, entity.getVariantCount());
        ps.setString(paramIndex++, entity.getChromosomeNumber());
        ps.setString(paramIndex++, entity.getSomaticStatus());
        ps.setString(paramIndex++, entity.getAminoAcidChange());
        ps.setObject(paramIndex++, entity.getChromosomeLocationStart());
        ps.setObject(paramIndex++, entity.getChromosomeLocationEnd());
        ps.setString(paramIndex++, entity.getVariantType());
        ps.setString(paramIndex++, entity.getCopyNumberAlterationType());
        ps.setString(paramIndex++, entity.getRearrGene1());
        ps.setString(paramIndex++, entity.getRearrDesc());
        ps.setObject(paramIndex++, entity.getTumourMutationBurden());

        ps.setObject(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(TABLE);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("bmr_sec_hash").build(),
                fieldBuilder.setField("bmr_date_updated").build(),

                fieldBuilder.setField("BMR_GENE").build(),
                fieldBuilder.setField("BMR_SPECIMEN_ID").build(),
                fieldBuilder.setField("BMR_SAMPLE_ID").build(),
                fieldBuilder.setField("BMR_CDNA_CHANGE").build(),
                fieldBuilder.setField("BMR_EXTERNAL_VARIANT_ID").build(),
                fieldBuilder.setField("BMR_TOTAL_READS").build(),
                fieldBuilder.setField("BMR_GERMLINE_FREQUENCY").build(),
                fieldBuilder.setField("BMR_MUTANT_ALLELE_FREQ").build(),
                fieldBuilder.setField("BMR_COPY_NUMBER").build(),
                fieldBuilder.setField("BMR_CIN_RANK").build(),
                fieldBuilder.setField("BMR_MUTATION_TYPE").build(),
                fieldBuilder.setField("BMR_VARIANT_COUNT").build(),
                fieldBuilder.setField("BMR_CHROMOSOME").build(),
                fieldBuilder.setField("BMR_SOMATIC_STATUS").build(),
                fieldBuilder.setField("BMR_AMINO_ACID_CHANGE").build(),
                fieldBuilder.setField("BMR_CHROMOSOME_LOCATION_START").build(),
                fieldBuilder.setField("BMR_CHROMOSOME_LOCATION_END").build(),
                fieldBuilder.setField("BMR_VARIANT_TYPE").build(),
                fieldBuilder.setField("BMR_CNA_TYPE").build(),
                fieldBuilder.setField("BMR_REARR_GENE_1").build(),
                fieldBuilder.setField("BMR_REARR_DESCRIPTION").build(),
                fieldBuilder.setField("BMR_TUMOUR_MUTATIONAL_BURDEN").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField(ID).build());
        return QueryBuilderUtil.buildUpdateQuery(TABLE, fieldsToSet, whereFields);
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
        return " select " + PREFIX + "_unq_sha1, " + PREFIX + "_sec_hash, " + ID
                + " from " + TABLE
                + " join result_patient on pat_id=" + PREFIX + "_pat_id "
                + " where pat_std_id=?";
    }

    private Integer parseToInt(String str) {
        Integer result;
        try {
            result = Integer.parseInt(str);

        } catch (NumberFormatException ex) {
            return null;
        }
        return result;
    }


}

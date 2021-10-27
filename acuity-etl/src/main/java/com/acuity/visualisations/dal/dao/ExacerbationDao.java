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
import com.acuity.visualisations.model.output.entities.Exacerbation;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class ExacerbationDao extends EntityDao<Exacerbation> {

    @Override
    public String getTableName() {
        return "result_exacerbation";
    }

    @Override
    protected String getTablePrefix() {
        return "exa";
    }

    @Override
    protected String getIdColumnName() {
        return "exa_id";
    }

    @Override
    public List<String> getSubjectsIdsByStudyName(String studyName) {
        return getJdbcTemplate().queryForList(
                "SELECT DISTINCT pat_id "
                        + " FROM result_exacerbation "
                        + " JOIN result_patient ON pat_id=exa_pat_id "
                        + " JOIN result_study ON std_id=pat_std_id "
                        + " WHERE std_name=?", String.class, studyName);
    }

    @Override
    protected String getHashesStatement(Class<?> entityClass) {
        return " select exa_unq_sha1, exa_sec_hash, exa_id "
                + " from result_exacerbation "
                + " join result_patient on pat_id=exa_pat_id "
                + " where pat_std_id=?";
    }

    @Override
    protected String getInsertStatement() {
        String tagetTable = "result_exacerbation";
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);

        List<TableField> fieldsToInsert = Arrays.asList(
                fieldBuilder.setField("exa_id").build(),
                fieldBuilder.setField("exa_unq_sha1").build(),
                fieldBuilder.setField("exa_sec_hash").build(),
                fieldBuilder.setField("exa_ref_sha1").build(),
                fieldBuilder.setField("exa_date_created").build(),
                fieldBuilder.setField("exa_date_updated").build(),
                fieldBuilder.setField("exa_pat_id").build(),

                fieldBuilder.setField("exa_exac_start_date").build(),
                fieldBuilder.setField("exa_exac_end_date").build(),
                fieldBuilder.setField("exa_ltpca").build(),
                fieldBuilder.setField("exa_ltpca_start_date").build(),
                fieldBuilder.setField("exa_ltpca_end_date").build(),
                fieldBuilder.setField("exa_ediary_alert_date").build(),
                fieldBuilder.setField("exa_depot_gcs").build(),
                fieldBuilder.setField("exa_depot_gcs_start_date").build(),
                fieldBuilder.setField("exa_syscort_trt").build(),
                fieldBuilder.setField("exa_syscort_trt_start_date").build(),
                fieldBuilder.setField("exa_syscort_trt_end_date").build(),
                fieldBuilder.setField("exa_antibiotics_trt").build(),
                fieldBuilder.setField("exa_antibiotics_trt_start_date").build(),
                fieldBuilder.setField("exa_antibiotics_trt_end_date").build(),
                fieldBuilder.setField("exa_emer_trt").build(),
                fieldBuilder.setField("exa_emer_trt_date").build(),
                fieldBuilder.setField("exa_hospit").build(),
                fieldBuilder.setField("exa_hospit_start_date").build(),
                fieldBuilder.setField("exa_hospit_end_date").build(),
                fieldBuilder.setField("exa_ics_trt").build(),
                fieldBuilder.setField("exa_ics_start_date").build(),
                fieldBuilder.setField("exa_ics_end_date").build(),
                fieldBuilder.setField("exa_severity").build()
        );

        return QueryBuilderUtil.buildInsertQuery(tagetTable, fieldsToInsert);
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Exacerbation entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setString(paramIndex++, entity.getSha1ForUniqueFields());
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setString(paramIndex++, entity.getFirstSha1ForReferencedFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getPatientGuid());

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getExacStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getExacEndDate()));
        ps.setString(paramIndex++, entity.getLtpca());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getLtpcaStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getLtpcaEndDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEdiaryAlertDate()));
        ps.setString(paramIndex++, entity.getDepotGcs());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDepotGcsStartDate()));
        ps.setString(paramIndex++, entity.getSyscortTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSyscortTrtStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSyscortTrtEndDate()));
        ps.setString(paramIndex++, entity.getAntibioticsTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAntibioticsTrtStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAntibioticsTrtEndDate()));
        ps.setString(paramIndex++, entity.getEmerTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEmerTrtDate()));
        ps.setString(paramIndex++, entity.getHospit());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitEndDate()));
        ps.setString(paramIndex++, entity.getIcsTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getIcsStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getIcsEndDate()));
        ps.setString(paramIndex++, entity.getSeverity());
    }


    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Exacerbation entity) throws SQLException {
        int paramIndex = 1;
        ps.setObject(paramIndex++, entity.getIntHashForSecondaryFields());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));

        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getExacEndDate()));
        ps.setString(paramIndex++, entity.getLtpca());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getLtpcaStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getLtpcaEndDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEdiaryAlertDate()));
        ps.setString(paramIndex++, entity.getDepotGcs());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDepotGcsStartDate()));
        ps.setString(paramIndex++, entity.getSyscortTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSyscortTrtStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getSyscortTrtEndDate()));
        ps.setString(paramIndex++, entity.getAntibioticsTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAntibioticsTrtStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getAntibioticsTrtEndDate()));
        ps.setString(paramIndex++, entity.getEmerTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getEmerTrtDate()));
        ps.setString(paramIndex++, entity.getHospit());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getHospitEndDate()));
        ps.setString(paramIndex++, entity.getIcsTrt());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getIcsStartDate()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getIcsEndDate()));
        ps.setString(paramIndex++, entity.getSeverity());
        ps.setString(paramIndex++, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = "result_exacerbation";
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        List<TableField> fieldsToSet = Arrays.asList(
                fieldBuilder.setField("exa_sec_hash").build(),
                fieldBuilder.setField("exa_date_updated").build(),

                fieldBuilder.setField("exa_exac_end_date").build(),
                fieldBuilder.setField("exa_ltpca").build(),
                fieldBuilder.setField("exa_ltpca_start_date").build(),
                fieldBuilder.setField("exa_ltpca_end_date").build(),
                fieldBuilder.setField("exa_ediary_alert_date").build(),
                fieldBuilder.setField("exa_depot_gcs").build(),
                fieldBuilder.setField("exa_depot_gcs_start_date").build(),
                fieldBuilder.setField("exa_syscort_trt").build(),
                fieldBuilder.setField("exa_syscort_trt_start_date").build(),
                fieldBuilder.setField("exa_syscort_trt_end_date").build(),
                fieldBuilder.setField("exa_antibiotics_trt").build(),
                fieldBuilder.setField("exa_antibiotics_trt_start_date").build(),
                fieldBuilder.setField("exa_antibiotics_trt_end_date").build(),
                fieldBuilder.setField("exa_emer_trt").build(),
                fieldBuilder.setField("exa_emer_trt_date").build(),
                fieldBuilder.setField("exa_hospit").build(),
                fieldBuilder.setField("exa_hospit_start_date").build(),
                fieldBuilder.setField("exa_hospit_end_date").build(),
                fieldBuilder.setField("exa_ics_trt").build(),
                fieldBuilder.setField("exa_ics_start_date").build(),
                fieldBuilder.setField("exa_ics_end_date").build(),
                fieldBuilder.setField("exa_severity").build()
        );
        List<TableField> whereFields = Arrays.asList(fieldBuilder.setField("exa_id").build());
        return QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
    }
}

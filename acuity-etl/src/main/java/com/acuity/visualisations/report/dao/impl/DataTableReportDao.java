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

package com.acuity.visualisations.report.dao.impl;

import com.acuity.visualisations.batch.holders.HolderResolver;
import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataTableReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportTableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO class for report data on ACUITY data tables
 */
@Repository
public class DataTableReportDao extends ACUITYDaoSupport implements IDataTableReportDao {

    @Autowired
    private HolderResolver holderResolver;

    /**
     * The SQL INSERT statement for inserting data in the database
     */
    private static final String INSERT_STATEMENT =
            "Insert into REPORT_DATA_TABLE (RDT_ID, RDT_JE_ID, RDT_DATA_SOURCE, RDT_ACUITY_ENTITIES, "
                    + "RDT_NUM_SUBJECT_SOURCE, RDT_NUM_SUBJECTS_ACUITY, RDT_NUM_EVENTS_UPLOADED, RDT_RAG_STATUS, RDT_FILE_SIZE) "
                    + "values (nextval('rdt_seq'),?,?,?,?,?,?,?,?)";

    /**
     * The SQL SELECT statement for select data in the database
     */
    private static final String SELECT_STATEMENT =
            "SELECT * FROM report_data_table d WHERE rdt_je_id = ?";

    /**
     * The delete outdated statement
     */
    private static final String DELETE_OUTDATED_STATEMENT =
            "DELETE FROM report_data_table WHERE rdt_je_id in (select job_execution_id from ( "
                    + "select job_execution_id, study, "
                    + "row_number() over (partition by study order by START_TIME DESC) as rank "
                    + "FROM batch_job_execution  "
                    + "LEFT OUTER JOIN  "
                    + "(SELECT  "
                    + "     job_execution_id AS params_exec_id,  "
                    + "     MAX(DECODE(key_name, 'etl.study', string_val)) AS study,  "
                    + "     MAX(DECODE(key_name, 'etl.project', string_val)) AS project  "
                    + "   FROM batch_job_execution_params GROUP BY job_execution_id) t1  "
                    + "    ON batch_job_execution.job_execution_id = t1.params_exec_id  "
                    + ") as k where rank > ? "
                    + "and study = ?)";
    /**
     * Maps selected database data to Java objects
     */
    private static final RowMapper<ReportTableEntity> ENTITY_ROW_MAPPER = (rs, rowNum) -> {
        ReportTableEntity out = new ReportTableEntity();

        out.setFileName(rs.getString("rdt_data_source"));
        out.setAcuityEntities(rs.getString("rdt_acuity_entities"));
        out.setNumSubjectsSource(rs.getInt("rdt_num_subject_source"));
        out.setNumSubjectsAcuity(rs.getInt("rdt_num_subjects_acuity"));
        out.setNumEventRowsUploaded(rs.getInt("rdt_num_events_uploaded"));
        out.setRagStatus(RagStatus.valueOf(rs.getString("rdt_rag_status")));
        out.setFileSize(rs.getLong("rdt_file_size"));
        return out;
    };

    /**
     * Inserts report data into the database
     *
     * @param jobExecutionId The batch job execution ID that the report corresponds to
     * @param reportData     The report data to insert
     */
    public void insertReportData(final Long jobExecutionId, final List<ReportTableEntity> reportData) {

        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, jobExecutionId);
                ps.setString(2, reportData.get(i).getFileName());
                ps.setString(3, reportData.get(i).getAcuityEntities());
                ps.setObject(4, reportData.get(i).getNumSubjectsSource());
                ps.setObject(5, reportData.get(i).getNumSubjectsAcuity());
                ps.setObject(6, reportData.get(i).getNumEventRowsUploaded());
                if (isEtlSkipped(jobExecutionId)
                        || (reportData.get(i).getNumSubjectsAcuity() != reportData.get(i).getNumSubjectsSource())) {
                    ps.setString(7, RagStatus.AMBER.toString());
                } else if (reportData.get(i).getNumSubjectsAcuity() == 0
                        || reportData.get(i).getNumSubjectsSource() == 0) {
                    ps.setString(7, RagStatus.RED.toString());
                } else {
                    ps.setString(7, RagStatus.GREEN.toString());
                }

                ps.setObject(8, reportData.get(i).getFileSize());
            }

            @Override
            public int getBatchSize() {
                return reportData.size();
            }
        };

        getJdbcTemplate().batchUpdate(INSERT_STATEMENT, bpss);
    }

    private boolean isEtlSkipped(Long jobExecutionId) {
        return !holderResolver.getConfigurationDataHolder(jobExecutionId)
                .getRuntimeConfiguration()
                .isEtlExecuted();
    }

    /**
     * Gets table report data from the database
     *
     * @param jobExecID The job execution ID
     * @return A list of ReportTableEntity objects
     */
    public List<ReportTableEntity> selectReportData(final int jobExecID) {
        return getJdbcTemplate().query(SELECT_STATEMENT, ENTITY_ROW_MAPPER, jobExecID);
    }

    @Override
    public void deleteOutdatedReportData(String studyCode, int keepCount) {
        getJdbcTemplate().update(DELETE_OUTDATED_STATEMENT, keepCount, studyCode);
    }
}

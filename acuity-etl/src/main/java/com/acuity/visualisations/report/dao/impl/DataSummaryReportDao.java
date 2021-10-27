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

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.report.dao.IDataSummaryReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportSummaryEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * This class contains functionality relating to database access for the ETL summary report
 */
@Repository
public class DataSummaryReportDao extends ACUITYDaoSupport implements IDataSummaryReportDao {

    /**
     * The select statement. Used to get ETL summary report data from the database
     */
    private static final String SELECT_STATEMENT =
            "SELECT r.*, j.exit_code, j.start_time, j.end_time "
                    + "FROM report_data_summary r "
                    + "LEFT JOIN batch_job_execution j ON r.rds_je_id = j.job_execution_id "
                    + "WHERE r.rds_study_code=?"
                    + "ORDER BY start_time DESC";

    /**
     * This selects the RAG status of an existing summary record
     */
    private static final String SELECT_RAG_STATUS_STATEMENT =
            "SELECT rds_rag_status FROM report_data_summary WHERE rds_je_id = ?";

    /**
     * This gets the number of records for a batch job execution
     */
    private static final String SELECT_RECORD_COUNT =
            "SELECT COUNT(*) FROM report_data_summary WHERE rds_je_id = ?";

    /**
     * The insert statement. It searches for warnings or errors in table, field and value
     * reports. It also checks the exit status for the batch job. The summary insert must
     * be called after the table, field and value reports have been inserted.
     */
    private static final String INSERT_FOR_COMPLETED_ETL_RUN_STATEMENT =
            "insert into report_data_summary (rds_id, rds_je_id, rds_study_code, rds_rag_status, rds_files_size, rds_files_count) "
                    + "values(nextval('rds_seq'), ?, ?, "
                    +
                    "(select case when "
                    + "exists (select 1 from report_data_table where rdt_je_id=?"
                    + "  and (rdt_num_subject_source=0 or rdt_num_subject_source!=rdt_num_subjects_acuity)) "
                    + "or exists (select 1 from report_data_field where rdf_je_id=?"
                    + "  and (rdf_error_type = 'MAPPING_ERROR' or rdf_error_type = 'DATA_ERROR')) "
                    + "or exists (select 1 from report_data_value where rdv_je_id=?"
                    + "  and (rdv_error_type = 'PARSE_WARNING' or rdv_error_type = 'PARSE_ERROR')) "
                    + "then 'AMBER' else 'GREEN' end)"
                    + ", ?, ?)";

    /**
     * The insert statement for ETL runs that have an exit code FAILED, COMPLETED_WITH_SKIPS or UNKNOWN
     */
    private static final String INSERT_FOR_UNSUCCESSFUL_ETL_RUN_STATEMENT =
            "INSERT INTO report_data_summary "
                    + "(rds_id, rds_je_id, rds_study_code, rds_rag_status, rds_files_size, rds_files_count) "
                    + "VALUES ("
                    + "nextval('rds_seq'), "
                    + "?, ?, ?, ?, ?)";

    /**
     * The delete outdated statement
     */
    private static final String DELETE_OUTDATED_STATEMENT =
            "DELETE FROM report_data_summary WHERE rds_je_id in (select job_execution_id from ( "
                    + "select job_execution_id, study, "
                    + "row_number() over (partition by study order by START_TIME DESC) as rank "
                    + "FROM batch_job_execution  "
                    + "LEFT OUTER JOIN  "
                    + "  (SELECT  "
                    + "     job_execution_id AS params_exec_id,  "
                    + "     MAX(DECODE(key_name, 'etl.study', string_val)) AS study,  "
                    + "     MAX(DECODE(key_name, 'etl.project', string_val)) AS project  "
                    + "   FROM batch_job_execution_params GROUP BY job_execution_id) t1  "
                    + "    ON batch_job_execution.job_execution_id = t1.params_exec_id  "
                    + ") as a where rank > ? "
                    + "and study = ?)";
    /**
     * Maps selected database data to Java objects
     */
    private static final RowMapper<ReportSummaryEntity> ENTITY_ROW_MAPPER = (rs, rowNum) -> {
        ReportSummaryEntity out = new ReportSummaryEntity();

        Timestamp startTimestamp = rs.getTimestamp("start_time");
        Timestamp endTimestamp = rs.getTimestamp("end_time");

        out.setJobExecID(rs.getInt("rds_je_id"));
        out.setStudyID(rs.getString("rds_study_code"));
        out.setExitCode(rs.getString("exit_code"));
        out.setStartDate(rs.getDate("start_time"));
        out.setEndDate(rs.getDate("end_time"));
        out.setRagStatus(RagStatus.valueOf(rs.getString("rds_rag_status")));
        out.setSummary(out.getRagStatus().getSummary());
        out.setFilesSize(rs.getLong("rds_files_size"));
        out.setFilesCount(rs.getInt("rds_files_count"));

        if (endTimestamp != null) {
            long duration = (endTimestamp.getTime() - startTimestamp.getTime()) / 1000;
            out.setDuration(String.format("%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60, duration % 60));
        }
        return out;
    };

    /**
     * Inserts report data for ETL runs that have an exit status FAILED, COMPLETED_WITH_SKIPS or UNKNOWN
     *
     * @param summary object to save
     */
    public void insertReportDataForUnsuccessfulEtlRun(ReportSummaryEntity summary) {

        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, summary.getJobExecID());
                ps.setString(2, summary.getStudyID());
                ps.setString(3, summary.getRagStatus().toString());
                ps.setLong(4, summary.getFilesSize());
                ps.setInt(5, summary.getFilesCount());
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        };

        getJdbcTemplate().batchUpdate(INSERT_FOR_UNSUCCESSFUL_ETL_RUN_STATEMENT, bpss);
    }

    /**
     * Inserts the summary report data into the database. The summary insert must
     * be called after the table, field and value reports have been inserted.
     *
     * @param summary object to save
     */
    public void insertReportDataForCompletedEtlRun(ReportSummaryEntity summary) {

        getJdbcTemplate().update(INSERT_FOR_COMPLETED_ETL_RUN_STATEMENT, ps -> {
            ps.setLong(1, summary.getJobExecID());
            ps.setString(2, summary.getStudyID());
            ps.setLong(3, summary.getJobExecID());
            ps.setLong(4, summary.getJobExecID());
            ps.setLong(5, summary.getJobExecID());
            ps.setLong(6, summary.getFilesSize());
            ps.setInt(7, summary.getFilesCount());
        });
    }

    /**
     * Gets the RAG status of an existing record
     *
     * @param jobExecID The batch job execution ID
     * @return The RAG status of the summary data for the batch job execution ID
     */
    public RagStatus selectRagStatus(final long jobExecID) {
        return RagStatus.valueOf(getJdbcTemplate().queryForObject(SELECT_RAG_STATUS_STATEMENT, new Object[]{jobExecID}, String.class));
    }

    /**
     * This determines whether there is summary report data present for the batch job execution
     *
     * @param jobExecID The batch job execution ID
     * @return True if there is more than zero records present, else false.
     */
    public boolean isReportDataPresent(final long jobExecID) {
        return getJdbcTemplate().queryForObject(SELECT_RECORD_COUNT, new Object[]{jobExecID}, Integer.class) > 0;
    }

    /**
     * Gets the report summary data from the database
     *
     * @param studyCode The study code to get the summary data for
     * @return A list of ReportSummaryEntity objects for the selected studyCode
     */
    public List<ReportSummaryEntity> selectReportData(final String studyCode) {
        return getJdbcTemplate().query(SELECT_STATEMENT, ENTITY_ROW_MAPPER, studyCode);
    }

    @Override
    public void deleteOutdatedReportData(String studyCode, int keepCount) {
        getJdbcTemplate().update(DELETE_OUTDATED_STATEMENT, keepCount, studyCode);
    }
}

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
import com.acuity.visualisations.report.dao.IExceptionReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportExceptionEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO class for reporting any exceptions that have been thrown during the ETL run
 */
@Repository
public class ExceptionReportDao extends ACUITYDaoSupport implements IExceptionReportDao {

    /**
     * The database insert statement
     */
    private static final String INSERT_STATEMENT =
            "INSERT INTO report_exceptions "
                    + "(rex_id, rex_je_id, rex_etl_step, rex_exception_type, rex_message, rex_stack_trace, rex_rag_status) "
                    + "VALUES(nextval('rex_seq'), ?, ?, ?, ?, ?, ?)";

    /**
     * The database select statement
     */
    private static final String SELECT_STATEMENT = "SELECT * FROM report_exceptions WHERE rex_je_id = ?";

    /**
     * The delete outdated statement
     */
    private static final String DELETE_OUTDATED_STATEMENT =
            "DELETE FROM report_exceptions WHERE rex_je_id in (select job_execution_id from ( "
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
                    + ") where rank > ? "
                    + "and study = ?)";
    /**
     * Maps selected database data to Java objects
     */
    private static final RowMapper<ReportExceptionEntity> ENTITY_ROW_MAPPER = (rs, rowNum) -> {
        ReportExceptionEntity out = new ReportExceptionEntity();

        out.setJobExecID(rs.getLong("rex_je_id"));
        out.setEtlStep(rs.getString("rex_etl_step"));
        out.setExceptionClass(rs.getString("rex_exception_type"));
        out.setRagStatus(RagStatus.valueOf(rs.getString("rex_rag_status")));
        out.setMessage(rs.getString("rex_message"));

        return out;
    };

    /**
     * Inserts exception report data into the database
     *
     * @param jobExecID  The job execution ID that this report data relates to
     * @param reportData A list of {@link ReportExceptionEntity} classes containing the exception report data
     */
    public void insertReportData(final long jobExecID, final List<ReportExceptionEntity> reportData) {

        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, jobExecID);
                ps.setString(2, reportData.get(i).getEtlStep());
                ps.setString(3, reportData.get(i).getExceptionClass());
                ps.setString(4, reportData.get(i).getMessage());
                ps.setString(5, reportData.get(i).getStackTrace());
                ps.setString(6, reportData.get(i).getRagStatus().toString());
            }

            @Override
            public int getBatchSize() {
                return reportData.size();
            }
        };

        getJdbcTemplate().batchUpdate(INSERT_STATEMENT, bpss);
    }

    /**
     * Select exception report data from the database
     *
     * @param joBExecID The job execution ID to get the report data for
     * @return A list of {@link ReportExceptionEntity} objects containing the report data
     */
    public List<ReportExceptionEntity> selectReportData(final long joBExecID) {
        return getJdbcTemplate().query(SELECT_STATEMENT, ENTITY_ROW_MAPPER, joBExecID);
    }

    @Override
    public void deleteOutdatedReportData(String studyCode, int keepCount) {
        getJdbcTemplate().update(DELETE_OUTDATED_STATEMENT, keepCount, studyCode);
    }
}

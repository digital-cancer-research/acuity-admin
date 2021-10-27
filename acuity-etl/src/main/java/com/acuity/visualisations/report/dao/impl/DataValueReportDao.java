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
import com.acuity.visualisations.report.dao.IDataValueReportDao;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ValueErrorType;
import com.acuity.visualisations.report.entity.ReportValueEntity;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class contains functionality for ETL value report database IO
 */
@Repository
public class DataValueReportDao extends ACUITYDaoSupport implements IDataValueReportDao {

    /**
     * The insert statement
     */
    static final String INSERT_STATEMENT =
            "Insert into REPORT_DATA_VALUE (RDV_ID, RDV_JE_ID, RDV_DATA_SOURCE, RDV_DATA_FIELD, RDV_RAW_DATA_COLUMN, "
                    + "RDV_RAW_DATA_VALUE, RDV_ERROR_TYPE, RDV_ERROR_DESCRIPTION, RDV_ERROR_COUNT, RDV_RAG_STATUS) "
                    + "values (nextval('rdv_seq'),?,?,?,?,?,?,?,?,?)";

    /**
     * The select statement
     */
    private static final String SELECT_STATEMENT =
            "SELECT * FROM report_data_value WHERE rdv_je_id = ?";

    /**
     * The delete outdated statement
     */
    private static final String DELETE_OUTDATED_STATEMENT =
            "DELETE FROM report_data_value WHERE rdv_je_id in (select job_execution_id from ( "
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
                    + ") as a where rank > ? "
                    + "and study = ?)";
    /**
     * Maps selected database data to Java objects
     */
    private static final RowMapper<ReportValueEntity> ENTITY_ROW_MAPPER = new RowMapper<ReportValueEntity>() {

        @Override
        public ReportValueEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReportValueEntity out = new ReportValueEntity();

            out.setFileName(rs.getString("rdv_data_source"));
            out.setDataField(rs.getString("rdv_data_field"));
            out.setRawDataColumn(rs.getString("rdv_raw_data_column"));
            out.setRawDataValue(rs.getString("rdv_raw_data_value"));
            out.setErrorType(ValueErrorType.valueOf((String) rs.getString("rdv_error_type")));
            out.setErrorDescription(rs.getString("rdv_error_description"));
            out.setErrorCount(rs.getInt("rdv_error_count"));
            out.setRagStatus(RagStatus.valueOf((String) rs.getString("rdv_rag_status")));
            return out;
        }
    };

    /**
     * Inserts report data into the database
     *
     * @param jobExecutionId The batch job execution ID
     * @param dataTableList  The report data to insert
     */
    public void insertReportData(final Long jobExecutionId, final List<ReportValueEntity> dataTableList) {
        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ReportValueEntity data = dataTableList.get(i);

                ps.setLong(1, jobExecutionId);
                ps.setString(2, data.getFileName());
                ps.setString(3, data.getDataField());
                ps.setString(4, data.getRawDataColumn());
                ps.setString(5, data.getRawDataValue());
                ps.setString(6, data.getErrorType().toString());
                ps.setString(7, data.getErrorDescription());
                ps.setObject(8, data.getErrorCount());

                switch (data.getErrorType()) {

                    case PARSE_WARNING:
                        ps.setString(9, RagStatus.AMBER.toString());
                        break;

                    case PARSE_ERROR:
                        ps.setString(9, RagStatus.RED.toString());
                        break;

                    default:
                        ps.setString(9, RagStatus.GREEN.toString());
                        break;
                }
            }

            @Override
            public int getBatchSize() {
                return dataTableList.size();
            }
        };

        getJdbcTemplate().batchUpdate(INSERT_STATEMENT, bpss);
    }

    public void deleteOutdatedReportData(String studyCode, int keepCount) {
        getJdbcTemplate().update(DELETE_OUTDATED_STATEMENT, keepCount, studyCode);
    }

    /**
     * Gets value report data from the database
     *
     * @param jobExecID The job execution ID
     * @return A list of ReportValueEntity objects
     */
    public List<ReportValueEntity> selectReportData(final int jobExecID) {
        return getJdbcTemplate().query(SELECT_STATEMENT, ENTITY_ROW_MAPPER, jobExecID);
    }
}

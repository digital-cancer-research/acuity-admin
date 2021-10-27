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
import com.acuity.visualisations.report.dao.IDataFieldReportDao;
import com.acuity.visualisations.report.entity.ColumnErrorType;
import com.acuity.visualisations.report.entity.RagStatus;
import com.acuity.visualisations.report.entity.ReportFieldEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO class that handles report field data IO
 */
@Repository
public class DataFieldReportDao extends ACUITYDaoSupport implements IDataFieldReportDao {

    /**
     * The insert statement
     */
    static final String INSERT_STATEMENT =
            "Insert into REPORT_DATA_FIELD (RDF_ID,RDF_JE_ID,RDF_DATA_SOURCE,RDF_DATA_FIELD,RDF_RAW_DATA_COLUMN,"
                    + "RDF_IS_MAPPED,RDF_ERROR_TYPE,RDF_ERROR_DESCRIPTION,RDF_RAG_STATUS) values (nextval('rdf_seq'),?,?,?,?,?,?,?,?)";

    /**
     * The select statement
     */
    private static final String SELECT_STATEMENT = "SELECT * FROM report_data_field WHERE rdf_je_id = ?";
    /**
     * The delete outdated statement
     */
    private static final String DELETE_OUTDATED_STATEMENT =
            "DELETE FROM report_data_field WHERE rdf_je_id in (select job_execution_id from ( "
                    + "select job_execution_id, study, "
                    + "row_number() over (partition by study order by START_TIME DESC) as rank "
                    + "FROM batch_job_execution  "
                    + "LEFT OUTER JOIN  "
                    + "  (SELECT  "
                    + "     job_execution_id AS params_exec_id,  "
                    + "     MAX(DECODE(key_name, 'etl.study', string_val)) AS study,  "
                    + "     MAX(DECODE(key_name, 'etl.project', string_val)) AS project  "
                    + "   FROM batch_job_execution_params GROUP BY job_execution_id) t1  "
                    + "    ON batch_job_execution.job_execution_id = t1.params_exec_id "
                    + ") as a where rank > ? "
                    + "and study = ?)";
    /**
     * Maps selected database data to Java objects
     */
    private static final RowMapper<ReportFieldEntity> ENTITY_ROW_MAPPER = (rs, rowNum) -> {
        ReportFieldEntity out = new ReportFieldEntity();

        out.setFileName(rs.getString("rdf_data_source"));
        out.setMapped(rs.getString("rdf_is_mapped").equals("1") ? true : false);
        out.setRawDataColumn(rs.getString("rdf_raw_data_column"));
        out.setDataField(rs.getString("rdf_data_field"));
        String rdfErrorType = rs.getString("rdf_error_type");
        if (rdfErrorType == null) {
            out.setErrorType(null);
        } else {
            out.setErrorType(ColumnErrorType.valueOf(rdfErrorType));
        }
        out.setErrorDescription(rs.getString("rdf_error_description"));
        String rdfRagStatus = rs.getString("rdf_rag_status");
        if (rdfRagStatus == null) {
            out.setRagStatus(null);
        } else {
            out.setRagStatus(RagStatus.valueOf(rdfRagStatus));
        }
        return out;
    };

    /**
     * Inserts data into the DB.
     *
     * @param jobExecutionId The job execution ID associated with the report
     * @param reportData     The data to be inserted
     */
    public void insertReportData(final Long jobExecutionId, final List<ReportFieldEntity> reportData) {

        BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ReportFieldEntity data = reportData.get(i);

                ps.setLong(1, jobExecutionId);
                ps.setString(2, data.getFileName());
                ps.setString(3, data.getDataField());
                ps.setString(4, data.getRawDataColumn());
                ps.setBoolean(5, data.isMapped());
                ps.setString(6, data.getErrorType() == null ? null : data.getErrorType().toString());
                ps.setString(7, data.getErrorDescription());

                if (data.getErrorType() == null) {
                    ps.setString(8, null);
                } else {
                    switch (data.getErrorType()) {

                        case MAPPING_ERROR:
                        case DATA_WARNING:
                            ps.setString(8, RagStatus.AMBER.toString());
                            break;

                        case DATA_ERROR:
                            ps.setString(8, RagStatus.RED.toString());
                            break;

                        case NO_ERROR:
                            if (data.isMapped()) {
                                ps.setString(8, RagStatus.GREEN.toString());
                            } else {
                                ps.setString(8, RagStatus.UNMAPPED.toString());
                            }
                            break;

                        default:
                            ps.setString(8, RagStatus.GREEN.toString());
                            break;
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return reportData.size();
            }
        };

        getJdbcTemplate().batchUpdate(INSERT_STATEMENT, bpss);
    }

    public void deleteOutdatedReportData(String studyCode, int keepCount) {
        getJdbcTemplate().update(DELETE_OUTDATED_STATEMENT, keepCount, studyCode);
    }

    /**
     * Gets field report data from the database
     *
     * @param jobExecID The job execution ID
     * @return A list of ReportFieldEntity objects
     */
    public List<ReportFieldEntity> selectReportData(final int jobExecID) {
        return getJdbcTemplate().query(SELECT_STATEMENT, ENTITY_ROW_MAPPER, jobExecID);
    }
}

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

package com.acuity.visualisations.web.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.util.JobLauncherConsts;
import com.acuity.visualisations.web.entity.BatchJobExecutionKey;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains functionality for accessing batch job execution data in
 * the database
 */
@Repository
public class CustomJobExecutionDao extends ACUITYDaoSupport implements ExtendedJobExecutionDao {

    private static final String GET_ALL_JOB_EXECUTIONS_QUERY = "select project, study, batch_job_execution.* from "
            + "batch_job_execution,"
            + "(select job_execution_id as job_params_jeid, "
            + "max(DECODE(key_name, '"
            + JobLauncherConsts.STUDY_KEY
            + "', string_val)) study, "
            + "max(DECODE(key_name, '"
            + JobLauncherConsts.PROJECT_KEY
            + "', string_val)) project from batch_job_execution_params group by job_execution_id) "
            + "where batch_job_execution.job_execution_id=job_params_jeid";

    private static final String GET_ALL_JOB_ECEXUTION_IDS_QUERY = "select project, study, batch_job_execution.JOB_EXECUTION_ID from "
            + "batch_job_execution,"
            + "(select job_execution_id as job_params_jeid, "
            + "max(DECODE(key_name, '"
            + JobLauncherConsts.STUDY_KEY
            + "', string_val)) study, "
            + "max(DECODE(key_name, '"
            + JobLauncherConsts.PROJECT_KEY
            + "', string_val)) project from batch_job_execution_params group by job_execution_id) "
            + "where batch_job_execution.job_execution_id=job_params_jeid";

    private static final String GET_LATEST_JOB_EXECUTION_QUERY = "with job_params_decoded as " 
            + "  (select job_execution_id       as job_params_jeid, "
            + "        max(decode(key_name, '"
            + JobLauncherConsts.STUDY_KEY
            + "', string_val))                  as study, "
            + "        max(decode(key_name, '"
            + JobLauncherConsts.PROJECT_KEY
            + "', string_val))                  as project "
            + "                            from batch_job_execution_params "
            + "                            group by job_execution_id) "
            + "select t1.project, "
            + "       t1.study, "
            + "       t2.job_execution_id, "
            + "       t2.version, "
            + "       t2.job_instance_id, "
            + "       t2.create_time, "
            + "       t2.start_time, "
            + "       t2.end_time, "
            + "       t2.status, "
            + "       t2.exit_code, "
            + "       t2.exit_message, "
            + "       t2.last_updated "
            + "from (select max(batch_job_execution.start_time) as start_time, project, study "
            + "      from batch_job_execution "
            + "               inner join batch_job_instance bji on batch_job_execution.job_instance_id = bji.job_instance_id "
            + "               inner join job_params_decoded on batch_job_execution.job_execution_id = job_params_jeid "
            + "          and bji.job_name = ? "
            + "      group by project, study) as t1 "
            + "         left outer join (select batch_job_execution.*, project, study "
            + "                          from batch_job_execution "
            + "                                   inner join job_params_decoded "
            + "                                   on batch_job_execution.job_execution_id = job_params_jeid) as t2 "
            + "         on t1.project = t2.project and t1.study = t2.study and t1.start_time = t2.start_time";

    private static final String GET_LATEST_JOB_EXECUTION_BY_KEY_QUERY = "select "
            + "t1.project, "
            + "t1.study, "
            + "t2.launchingTime, "
            + "t2.JOB_EXECUTION_ID, "
            + "t2.VERSION, "
            + "t2.JOB_INSTANCE_ID, "
            + "t2.CREATE_TIME, "
            + "t2.START_TIME, "
            + "t2.END_TIME, "
            + "t2.STATUS, "
            + "t2.EXIT_CODE, "
            + "t2.EXIT_MESSAGE, "
            + "t2.LAST_UPDATED "
            + "from "
            + "(select max(batch_job_execution.start_time) as START_TIME,project, study "
            + "from batch_job_execution "
            + "INNER JOIN batch_job_instance bji"
            + "  ON batch_job_execution.job_instance_id = bji.job_instance_id, "
            + "(select job_execution_id as job_params_jeid,max(DECODE(key_name, '"
            + JobLauncherConsts.STUDY_KEY
            + "', string_val)) study, max(DECODE(key_name, '"
            + JobLauncherConsts.PROJECT_KEY
            + "', string_val)) project "
            + "from batch_job_execution_params group by job_execution_id) as a "
            + "where batch_job_execution.job_execution_id=job_params_jeid "
            + "and bji.job_name = ? "
            + "group by project, study) "
            + "t1 "
            + "left outer join "
            + "(select batch_job_execution.*, project, study, launchingTime "
            + "from batch_job_execution, "
            + "(select job_execution_id as job_params_jeid,max(DECODE(key_name, '"
            + JobLauncherConsts.STUDY_KEY
            + "', string_val)) study, max(DECODE(key_name, '"
            + JobLauncherConsts.PROJECT_KEY
            + "', string_val)) project, max(DECODE(key_name, '"
            + JobLauncherConsts.UNIQUE_KEY
            + "', string_val)) launchingTime "
            + "from batch_job_execution_params group by job_execution_id) as b "
            + "where batch_job_execution.job_execution_id=job_params_jeid) "
            + " t2 "
            + "on t1.project = t2.project and t1.study=t2.study and t1.START_TIME = t2.START_TIME";

    private static final String GET_JOB_EXEC_IDS_FOR_STUDY_SQL =
            "SELECT e.job_execution_id "
                    + "FROM batch_job_execution e "
                    + "RIGHT JOIN batch_job_execution_params p ON e.job_execution_id = p.job_execution_id "
                    + "WHERE p.key_name='etl.study' AND p.string_val = ? "
                    + "ORDER BY e.job_execution_id";

    private static final String GET_FAILED_STUDIES =
            "with enabled_studies as (select msr_study_code from map_study_rule "
                    + "           inner join map_project_rule on (msr_prj_id = mpr_id) "
                    + "           where msr_enabled = 1), "
                    + "     bath_job_data as (select batch_job_execution.end_time         as c_e_time, "
                    + "                              batch_job_execution.status           as c_j_status, "
                    + "                              batch_job_execution.job_execution_id as c_j_id "
                    + "                       from batch_job_execution), "
                    + "     studies as ( "
                    + "         select distinct on (msr_study_code) msr_study_code as std_name, "
                    + "                                             c_j_status     as last_status "
                    + "         from batch_job_execution_params left join bath_job_data "
                    + "                            on batch_job_execution_params.job_execution_id = c_j_id "
                    + "                  inner join enabled_studies "
                    + "                            on enabled_studies.msr_study_code = batch_job_execution_params.string_val "
                    + "         order by msr_study_code, c_e_time desc nulls last "
                    + "     ) "
                    + "    select distinct std_name "
                    + "    from studies "
                    + "    where last_status = 'FAILED'";

    private static final String GET_NOT_RUN_STUDIES =
            "with enabled_studies as (select msr_study_code from map_study_rule "
                    + "                                  inner join map_project_rule on (msr_prj_id = mpr_id) "
                    + "                         where msr_enabled = 1 and msr_scheduled = true), "
                    + "     bath_job_data as (select batch_job_execution.end_time         as c_e_time, "
                    + "                              batch_job_execution.status           as c_j_status, "
                    + "                              batch_job_execution.job_execution_id as c_j_id "
                    + "                       from batch_job_execution), "
                    + "     studies as ( "
                    + "         select distinct on (msr_study_code) msr_study_code as std_name, "
                    + "                                             c_e_time       as last_run_time, "
                    + "                                             c_j_status     as last_status "
                    + "         from batch_job_execution_params "
                    + "                  left join bath_job_data "
                    + "                            on batch_job_execution_params.job_execution_id = c_j_id "
                    + "                  inner join enabled_studies "
                    + "                             on enabled_studies.msr_study_code = batch_job_execution_params.string_val "
                    + "         order by msr_study_code, c_e_time desc nulls last "
                    + "     ) "
                    + "    select distinct std_name "
                    + "    from studies "
                    + "    where last_run_time is null "
                    + "         or oracle.sysdate() > (last_run_time + interval '1' day)::date";

    private static final String GET_STUDIES_FILES = "select MSR_STUDY_CODE, MFR_NAME "
            + "from MAP_STUDY_RULE "
            + "inner join MAP_FILE_RULE on (msr_id = MFR_MSR_ID) "
            + "where MSR_SCHEDULED = true and MSR_ENABLED = 1 "
            + "order by MSR_STUDY_CODE";

    private static final ResultSetExtractor<Map<String, Map<String, List<JobExecution>>>> EXPANDED_MAP_RESULT_SET_EXTRACTOR = rs -> {
        Map<String, Map<String, List<JobExecution>>> result = new HashMap<String, Map<String, List<JobExecution>>>();
        while (rs.next()) {
            String projectName = rs.getString("PROJECT");
            if (!result.containsKey(projectName)) {
                result.put(projectName, new HashMap<String, List<JobExecution>>());
            }
            Map<String, List<JobExecution>> resultByProject = result.get(projectName);
            String studyName = rs.getString("STUDY");
            if (!resultByProject.containsKey(studyName)) {
                resultByProject.put(studyName, new ArrayList<JobExecution>());
            }
            List<JobExecution> executions = resultByProject.get(studyName);
            JobExecution jobExecution = getJobExecutionFromResultSet(rs);
            executions.add(jobExecution);
        }
        return result;
    };

    private static final ResultSetExtractor<Map<String, Map<String, JobExecution>>> EXPANDED_LIST_RESULT_SET_EXTRACTOR = rs -> {
        Map<String, Map<String, JobExecution>> result = new HashMap<String, Map<String, JobExecution>>();
        while (rs.next()) {
            String projectName = rs.getString("PROJECT");
            if (!result.containsKey(projectName)) {
                result.put(projectName, new HashMap<String, JobExecution>());
            }
            Map<String, JobExecution> resultByProject = result.get(projectName);
            String studyName = rs.getString("STUDY");
            JobExecution jobExecution = getJobExecutionFromResultSet(rs);
            resultByProject.put(studyName, jobExecution);
        }
        return result;
    };

    private static final ResultSetExtractor<Map<BatchJobExecutionKey, JobExecution>> FULL_KEY_RESULT_SET_EXTRACTOR = rs -> {
        Map<BatchJobExecutionKey, JobExecution> result = new HashMap<BatchJobExecutionKey, JobExecution>();
        while (rs.next()) {
            BatchJobExecutionKey key = getBatchJobExecutionKeyFromResultSet(rs);
            JobExecution jobExecution = getJobExecutionFromResultSet(rs);
            result.put(key, jobExecution);
        }
        return result;
    };

    private static final RowMapper<JobExecution> ROW_MAPPER = (rs, rowNum) -> getJobExecutionFromResultSet(rs);

    private static JobExecution getJobExecutionFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("JOB_EXECUTION_ID");
        JobExecution jobExecution;
        jobExecution = new JobExecution(id);
        jobExecution.setStartTime(rs.getTimestamp("START_TIME"));
        jobExecution.setEndTime(rs.getTimestamp("END_TIME"));
        String status = rs.getString("STATUS");
        if (status != null) {
            jobExecution.setStatus(BatchStatus.valueOf(status));
        }
        jobExecution.setExitStatus(new ExitStatus(rs.getString("EXIT_CODE"), rs.getString("EXIT_MESSAGE")));
        jobExecution.setCreateTime(rs.getTimestamp("CREATE_TIME"));
        jobExecution.setLastUpdated(rs.getTimestamp("CREATE_TIME"));
        jobExecution.setVersion(rs.getInt("VERSION"));
        return jobExecution;
    }

    private static BatchJobExecutionKey getBatchJobExecutionKeyFromResultSet(ResultSet rs) throws SQLException {
        String projectName = rs.getString("PROJECT");
        String studyCode = rs.getString("STUDY");
        String launchingTime = rs.getString("LAUNCHINGTIME");
        return new BatchJobExecutionKey(projectName, studyCode, launchingTime);
    }


    public List<JobExecution> getJobExecutions(final String projectName, final String studyCode) {
        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindJobExecutions());
            ps.setString(1, projectName);
            ps.setString(2, studyCode);
            return ps;
        }, ROW_MAPPER);
    }

    /**
     * Gets the batch job execution IDs for a given study
     *
     * @param studyCode The clinical study code
     * @return A list of Job Execution IDs for the given study
     */
    public List<Integer> getJobExecutionsByStudy(final String studyCode) {

        List<Integer> result = new ArrayList<Integer>();

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(GET_JOB_EXEC_IDS_FOR_STUDY_SQL, studyCode);

        for (Map<String, Object> row : rows) {
            result.add(((BigDecimal) row.get("job_execution_id")).intValue());
        }

        return result;
    }

    public Map<String, Map<String, List<JobExecution>>> getJobExecutions(final Map<String, List<String>> keys) {
        return getJdbcTemplate().query(con -> {
            int keysSize = 0;
            for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                keysSize += entry.getValue().size();
            }
            PreparedStatement ps = con.prepareStatement(getSQLToFindJobExecutions(keysSize));
            int index = 0;
            for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                for (String study : entry.getValue()) {
                    ps.setString(++index, entry.getKey());
                    ps.setString(++index, study);
                }
            }
            return ps;
        }, EXPANDED_MAP_RESULT_SET_EXTRACTOR);
    }

    public Map<String, Map<String, List<Long>>> getJobExecutionIds(final Map<String, List<String>> keys) {
        return getJdbcTemplate().query(con -> {
                    int keysSize = 0;
                    for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                        keysSize += entry.getValue().size();
                    }
                    PreparedStatement ps = con.prepareStatement(getSQLToFindJobExecutionIds(keysSize));
                    int index = 0;
                    for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                        for (String study : entry.getValue()) {
                            ps.setString(++index, entry.getKey());
                            ps.setString(++index, study);
                        }
                    }
                    return ps;
                }, (ResultSet rs) -> {
                    Map<String, Map<String, List<Long>>> result = new HashMap<String, Map<String, List<Long>>>();
                    while (rs.next()) {
                        String projectName = rs.getString("PROJECT");
                        if (!result.containsKey(projectName)) {
                            result.put(projectName, new HashMap<>());
                        }
                        Map<String, List<Long>> resultByProject = result.get(projectName);
                        String studyName = rs.getString("STUDY");
                        resultByProject.putIfAbsent(studyName, new ArrayList<>());
                        List<Long> executionIds = resultByProject.get(studyName);
                        Long jobExecutionId = rs.getLong("JOB_EXECUTION_ID");
                        executionIds.add(jobExecutionId);
                    }
                    return result;
                }
        );
    }

    public JobExecution getLatestJobExecution(final String projectName, final String studyCode, final String jobName) {
        List<JobExecution> query = getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindLatestJobExecution());
            ps.setString(1, jobName);
            ps.setString(2, projectName);
            ps.setString(3, studyCode);
            return ps;
        }, ROW_MAPPER);
        if (query.isEmpty()) {
            return null;
        }
        return query.get(0);
    }

    public Map<String, Map<String, JobExecution>> getLatestJobExecution(final Map<String, List<String>> keys, String jobName) {
        return getJdbcTemplate().query(con -> {
            int keysSize = 0;
            for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                keysSize += entry.getValue().size();
            }
            PreparedStatement ps = con.prepareStatement(getSQLToFindLatestJobExecution(keysSize));
            int index = 0;
            ps.setString(++index, jobName);
            for (Map.Entry<String, List<String>> entry : keys.entrySet()) {
                for (String study : entry.getValue()) {
                    ps.setString(++index, entry.getKey());
                    ps.setString(++index, study);
                }
            }
            return ps;
        }, EXPANDED_LIST_RESULT_SET_EXTRACTOR);
    }

    public Map<BatchJobExecutionKey, JobExecution> getLatestJobExecutionByFullKeyAndJobName(final List<BatchJobExecutionKey> keys, final String jobName) {
        return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSQLToFindLatestJobExecutionByFullKey(keys.size()));
            int index = 0;
            ps.setString(++index, jobName);
            for (BatchJobExecutionKey key : keys) {
                ps.setString(++index, key.getProjectName());
                ps.setString(++index, key.getStudyCode());
                ps.setString(++index, key.getJobExecutionGuid());
            }
            return ps;
        }, FULL_KEY_RESULT_SET_EXTRACTOR);
    }

    @Override
    public List<String> getFailedStudies() {
        return getJdbcTemplate().queryForList(GET_FAILED_STUDIES, String.class);
    }

    @Override
    public List<String> getNotRunStudies() {
        return getJdbcTemplate().queryForList(GET_NOT_RUN_STUDIES, String.class);
    }

    @Override
    public Map<String, List<String>> getScheduledStudiesFilenames() {
        List<Map<String, Object>> files = getJdbcTemplate().queryForList(GET_STUDIES_FILES);
        Map<String, List<String>> groupedFiles = new HashMap<>();
        files.forEach(f -> groupedFiles.computeIfAbsent((String) f.get("MSR_STUDY_CODE"), s -> new ArrayList()).add((String) f.get("MFR_NAME")));
        return groupedFiles;
    }

    protected String getSQLToFindLatestJobExecution(int keysNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(GET_LATEST_JOB_EXECUTION_QUERY);
        if (keysNumber > 0) {
            builder.append(" where (t1.project, t1.study) in ((?, ?)");
            for (int i = 1; i < keysNumber; i++) {
                builder.append(",(?, ?)");
            }
            builder.append(")");
        }
        return builder.toString();
    }

    private String getSQLToFindLatestJobExecution() {
        return GET_LATEST_JOB_EXECUTION_QUERY + " where t1.study=? and t1.project=?";
    }

    private String getSQLToFindJobExecutions() {
        return GET_ALL_JOB_EXECUTIONS_QUERY + " and project=? and study=?";
    }

    private String getSQLToFindJobExecutions(int keysNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(GET_ALL_JOB_EXECUTIONS_QUERY + " and (project, study) in (");
        for (int i = 0; i < keysNumber; i++) {
            builder.append("(?, ?), ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(")");
        return builder.toString();
    }

    /**
     * 4 parameters required, first is jabName
     */
    private String getSQLToFindLatestJobExecutionByFullKey(int keysNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(GET_LATEST_JOB_EXECUTION_BY_KEY_QUERY + " where (t1.project, t1.study, t2.launchingTime) in (");
        for (int i = 0; i < keysNumber; i++) {
            builder.append("(?, ?, ?), ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(")");
        return builder.toString();
    }

    private String getSQLToFindJobExecutionIds(int keysNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(GET_ALL_JOB_ECEXUTION_IDS_QUERY + " and (project, study) in (");
        for (int i = 0; i < keysNumber; i++) {
            builder.append("(?, ?), ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void insertScheduledCleanEtlStatus(String status, Long jobId) {
        String query = "UPDATE BATCH_JOB_EXECUTION SET CLEAN_STATUS=? WHERE JOB_EXECUTION_ID=?";
        getJdbcTemplate().update(query, status, jobId);
    }
}

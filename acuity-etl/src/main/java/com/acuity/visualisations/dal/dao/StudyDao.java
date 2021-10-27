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

import com.acuity.visualisations.dal.BasicEntityDao;
import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.model.output.entities.Study;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.data.util.Util.getSQLTimestamp;

@Repository
public class StudyDao extends BasicEntityDao<Study> implements IStudyDaoExternal {

    private String GET_DATASET_ID = "SELECT msr_id FROM map_study_rule where msr_study_code = ?";

    private static final String SELECT_STUDY_CLEAN_SCHEDULED_FLAG = "SELECT DISTINCT MPR_DRUG, MSR_STUDY_CODE, MSR_CLEAN_SCHEDULED "
            + "FROM MAP_PROJECT_RULE LEFT JOIN MAP_STUDY_RULE ON MAP_PROJECT_RULE.MPR_ID = MAP_STUDY_RULE.MSR_PRJ_ID";
    private static final String SELECT_STUDY_AML_FLAG = "SELECT DISTINCT MPR_DRUG, MSR_STUDY_CODE, MSR_AML_ENABLED "
            + "FROM MAP_PROJECT_RULE LEFT JOIN MAP_STUDY_RULE ON MAP_PROJECT_RULE.MPR_ID = MAP_STUDY_RULE.MSR_PRJ_ID";

    private static String[] deleteSequence = {
            "DELETE FROM result_source WHERE src_id IN"
                    + " (SELECT DISTINCT lab_src_id FROM result_laboratory WHERE lab_tst_id IN"
                    + " (SELECT tst_id FROM result_test INNER JOIN RESULT_PATIENT ON tst_pat_id = pat_id"
                    + " INNER JOIN result_study ON std_id = pat_std_id WHERE std_name = ?))",
            "DELETE FROM result_source WHERE src_id IN"
                    + " (SELECT DISTINCT rd_src_id FROM result_patient_reported_data WHERE rd_pat_id IN"
                    + " (SELECT pat_id FROM result_patient"
                    + " INNER JOIN result_study ON std_id = pat_std_id WHERE std_name = ?))",
            "DELETE FROM result_study WHERE std_name = ?"
    };

    public Long getDatasetIdByStudyCode(String studyCode) {
        return getJdbcTemplate().queryForObject(GET_DATASET_ID, Long.class, studyCode);
    }

    public void delete(String study) {
        logger.info("Cleaning study data for {} started", study);
        for (String statement : deleteSequence) {
            logger.debug("{}: {}", study, statement);
            getJdbcTemplate().update(statement, study);
        }
        logger.info("Cleaning study data for {} completed", study);
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "RESULT_STUDY";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);

        fieldsToInsert.add(fieldBuilder.setField("STD_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_DATE_CREATED").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_DATE_UPDATED").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_DISPLAY").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_PRJ_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("STD_STG_ID").build());

        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected String getUpdateStatement() {
        String tagetTable = "RESULT_STUDY";
        List<TableField> fieldsToSet = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(tagetTable);
        fieldsToSet.add(fieldBuilder.setField("STD_DATE_UPDATED").build());
        List<TableField> whereFields = new ArrayList<TableField>();
        whereFields.add(fieldBuilder.setField("STD_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(tagetTable, fieldsToSet, whereFields);
        return sql;
    }

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, Study entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getId());
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateCreated()));
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex++, entity.getStudyName());
        ps.setString(paramIndex++, entity.getStudyDisplay());
        ps.setString(paramIndex++, entity.getProjectGuid());
        ps.setString(paramIndex, entity.getStudyGroupGuid());
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, Study entity) throws SQLException {
        int paramIndex = 1;
        ps.setTimestamp(paramIndex++, getSQLTimestamp(entity.getDateUpdated()));
        ps.setString(paramIndex, entity.getId());
    }

    @Override
    protected String getIdColumnName() {
        return "STD_ID";
    }

    public String getStudyGuid(final String studyName, final String projectGuid) {
        List<String> list = getJdbcTemplate().query(con -> {
                    PreparedStatement ps = con.prepareStatement("select STD_ID from RESULT_STUDY where STD_NAME=? and STD_PRJ_ID=?");
                    ps.setString(1, studyName);
                    ps.setString(2, projectGuid);
                    return ps;
                }, (rs, rowNum) -> rs.getString("STD_ID")
        );
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public Date getStudyLastUploadDate(final String studyName, final String projectGuid) {
        return getJdbcTemplate().queryForObject("select STD_DATE_LAST_UPLOADED from RESULT_STUDY where STD_NAME=? and STD_PRJ_ID=?", Date.class,
                studyName, projectGuid);
    }

    public String getStudyLastUploadState(final String studyName, Long currentJobId) {
        final List<String> strings = getJdbcTemplate().queryForList("SELECT  t2.STATUS "
                + "FROM "
                + "  (SELECT MAX(batch_job_execution.start_time) AS START_TIME, "
                + "    project, "
                + "    study "
                + "  FROM batch_job_execution, "
                + "    (SELECT job_execution_id AS job_params_jeid, "
                + "      MAX(DECODE(key_name, 'etl.study', string_val)) study, "
                + "      MAX(DECODE(key_name, 'etl.project', string_val)) project "
                + "    FROM batch_job_execution_params "
                + "    GROUP BY job_execution_id "
                + "    ) as TempTable "
                + "  WHERE batch_job_execution.job_execution_id = job_params_jeid"
                + " and job_execution_id != ?"
                + "  GROUP BY project, "
                + "    study "
                + "  ) t1 "
                + "LEFT OUTER JOIN "
                + "  (SELECT batch_job_execution.*, "
                + "    project, "
                + "    study "
                + "  FROM batch_job_execution, "
                + "    (SELECT job_execution_id AS job_params_jeid, "
                + "      MAX(DECODE(key_name, 'etl.study', string_val)) study, "
                + "      MAX(DECODE(key_name, 'etl.project', string_val)) project "
                + "    FROM batch_job_execution_params "
                + "    GROUP BY job_execution_id "
                + "    ) as TempTableTwo"
                + "  WHERE batch_job_execution.job_execution_id = job_params_jeid "
                + "  ) t2 "
                + "ON t1.project     = t2.project "
                + "AND t1.study      = t2.study "
                + "AND t1.START_TIME = t2.START_TIME "
                + "where t1.study = ?", String.class, currentJobId, studyName);
        return strings.stream().findFirst().orElse(null);
    }

    public Date getStudyMappingModifiedDate(String studyName, String projectGuid) {
        return getJdbcTemplate().queryForObject("select rule.msr_mapping_modified_date "
                        + "from map_study_rule rule "
                        + "join result_study std "
                        + "on std.std_name = rule.msr_study_code "
                        + "where std.STD_NAME=? and std.STD_PRJ_ID=?", Date.class,
                studyName, projectGuid);
    }

    public Map<String, Map<String, Integer>> getStudyCount(final Map<String, List<String>> studies) {
        return getJdbcTemplate().query(con -> {
                    int studyCount = 0;
                    for (Map.Entry<String, List<String>> entry : studies.entrySet()) {
                        studyCount += entry.getValue().size();
                    }
                    PreparedStatement ps = con.prepareStatement(getCountStatement(studyCount));
                    int index = 0;
                    for (Map.Entry<String, List<String>> entry : studies.entrySet()) {
                        String projectName = entry.getKey();
                        for (String studyName : entry.getValue()) {
                            ps.setString(++index, projectName);
                            ps.setString(++index, studyName);
                        }
                    }
                    return ps;
                }, (ResultSet rs) -> {
                    Map<String, Map<String, Integer>> result = new HashMap<>();
                    while (rs.next()) {
                        String projectName = rs.getString("PRJ_NAME");
                        if (!result.containsKey(projectName)) {
                            result.put(projectName, new HashMap<>());
                        }
                        Map<String, Integer> resultByProject = result.get(projectName);
                        String studyName = rs.getString("STD_NAME");
                        Integer count = rs.getInt("STD_COUNT");
                        resultByProject.put(studyName, count);
                    }
                    return result;
                }
        );
    }

    @Override
    public boolean studyExists(String studyCode) {
        return getJdbcTemplate()
                .queryForObject("select count(*) from RESULT_STUDY inner join RESULT_PATIENT on PAT_STD_ID = STD_ID where STD_NAME = ?",
                        Integer.class, studyCode) > 0;
    }

    @Override
    public Map<String, Map<String, Boolean>> getStudyScheduledCleanFlag(final Map<String, List<String>> studies) {
        return getJdbcTemplate()
                .query(con -> con.prepareStatement(SELECT_STUDY_CLEAN_SCHEDULED_FLAG),
                        (ResultSet rs) -> aggregateFlagsByProgramByStudy(rs, "MSR_CLEAN_SCHEDULED"));
    }

    @Override
    public Map<String, Map<String, Boolean>> getStudyAmlEnabledFlag() {
        return getJdbcTemplate()
                .query(con -> con.prepareStatement(SELECT_STUDY_AML_FLAG),
                        (ResultSet rs) -> aggregateFlagsByProgramByStudy(rs, "MSR_AML_ENABLED"));
    }

    private Map<String, Map<String, Boolean>> aggregateFlagsByProgramByStudy(ResultSet rs, String flagFieldName) throws SQLException {
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        while (rs.next()) {
            String projectName = rs.getString("MPR_DRUG");
            result.putIfAbsent(projectName, new HashMap<>());
            Map<String, Boolean> resultByProject = result.get(projectName);
            String studyName = rs.getString("MSR_STUDY_CODE");
            resultByProject.put(studyName, rs.getBoolean(flagFieldName));
        }
        return result;
    }

    public void setScheduleCleanFlag(String study) {
        String query = "UPDATE MAP_STUDY_RULE SET MSR_CLEAN_SCHEDULED = "
                + "(CASE WHEN MSR_CLEAN_SCHEDULED=true THEN false ELSE true END) WHERE MSR_STUDY_CODE IN (?) ";
        getJdbcTemplate().update(query, study);
        logger.info("Scheduled clean flag was updated for study: {}", study);
    }

    public void setScheduleCleanFlag(String study, Boolean flag) {
        String query = "UPDATE MAP_STUDY_RULE SET MSR_CLEAN_SCHEDULED =? WHERE MSR_STUDY_CODE IN (?) ";
        getJdbcTemplate().update(query, flag, study);
        logger.info("Scheduled clean flag {} was set for study: {}", flag, study);
    }


    public int resetStudyEtlStatus(String studyCode) {
        final String sql = "UPDATE BATCH_JOB_EXECUTION "
                + "SET STATUS = 'FAILED', END_TIME = START_TIME "
                + "WHERE JOB_EXECUTION_ID IN "
                + "(SELECT JOB_EXECUTION_ID "
                + "FROM "
                + "(SELECT "
                + "row_number() over() as ROWNUM, "
                + "bjp.KEY_NAME, "
                + "bjp.STRING_VAL, "
                + "bje.JOB_INSTANCE_ID, "
                + "bje.JOB_EXECUTION_ID, "
                + "bje.START_TIME, "
                + "bje.END_TIME, "
                + "bje.STATUS "
                + "FROM "
                + "BATCH_JOB_EXECUTION_PARAMS bjp, "
                + "BATCH_JOB_EXECUTION bje "
                + "WHERE "
                + "bjp.JOB_EXECUTION_ID = bje.JOB_EXECUTION_ID "
                + "AND "
                + "bjp.KEY_NAME LIKE 'etl.study' "
                + "AND "
                + "bjp.STRING_VAL LIKE ? "
                + "AND "
                + "bje.STATUS = 'STARTED' "
                + "AND "
                + "bje.END_TIME IS NULL "
                + "ORDER BY bje.START_TIME DESC) as TempTable "
                + "WHERE ROWNUM = 1 "
                + ")";
        return getJdbcTemplate().update(sql, studyCode);
    }

    private String getCountStatement(int studyCount) {
        StringBuilder builder = new StringBuilder();
        builder.append("select RESULT_PROJECT.PRJ_NAME, RESULT_STUDY.STD_NAME, count(distinct STD_ID) STD_COUNT"
                + " from RESULT_STUDY INNER JOIN RESULT_PROJECT ON STD_PRJ_ID = PRJ_ID");
        if (studyCount > 0) {
            builder.append(" where (RESULT_PROJECT.prj_name, RESULT_STUDY.std_name) in ((?, ?)");
            for (int i = 1; i < studyCount; i++) {
                builder.append(",(?, ?)");
            }
            builder.append(")");
        }
        builder.append(" group by RESULT_PROJECT.PRJ_NAME, RESULT_STUDY.STD_NAME");
        return builder.toString();
    }

    @Override
    protected String getTableName() {
        return "RESULT_STUDY";
    }

}

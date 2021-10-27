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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.util.QueryBuilderUtil;
import com.acuity.visualisations.dal.util.TableField;
import com.acuity.visualisations.dal.util.TableFieldBuilder;
import com.acuity.visualisations.data.util.Util;
import com.acuity.visualisations.mapping.StudyType;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudyRule.Status;
import com.acuity.visualisations.util.ETLStudyRule;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class StudyRuleDao extends BasicDynamicEntityDao<StudyRule> implements IStudyRuleDao {
    private static final String SEARCH_BY_STUDY_CODE_QUERY
            = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) WHERE MSR_STUDY_CODE = ?";
    private static final String SEARCH_BY_PROJECT_ID_QUERY
            = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) WHERE MSR_PRJ_ID = ?";
    private static final String SEARCH_FOR_ETL_EXECUTE_QUERY
            = "SELECT mpr_drug, msr_study_code, MSR_SCHEDULED, MSR_CRON_EXPRESSION, MSR_CLEAN_SCHEDULED, MSR_AML_ENABLED"
            + " FROM MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  inner join map_project_rule on (mpr_id = msr_prj_id) WHERE msr_enabled = 1";
    private static final String SEARCH_BY_PROJECT_NAME_AND_STUDY_CODE_QUERY
            = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) WHERE MPR_DRUG = ? AND MSR_STUDY_CODE = ?";
    private static final String UNBIND_QUERY = "update MAP_STUDY_RULE SET MSR_PRJ_ID = ? where MSR_PRJ_ID = null";
    private static final String SEARCH_BY_CODE_QUERY
            = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) WHERE MSR_STUDY_CODE = ? ";
    private static final String STUDY_DELETE_QUERY = "delete from MAP_STUDY_RULE where MSR_STUDY_CODE=?";
    private static final String SEARCH_STUDY_QUERY
            = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) where (upper(MSR_STUDY_CODE) LIKE upper(?) or upper(MPR_DRUG) LIKE upper(?)) ";

    private final RowMapper<StudyRule> studyRowMapper = (rs, rowNum) -> {
        StudyRule study = new StudyRule();
        study.setId(rs.getLong("MSR_ID"));
        study.setStudyName(rs.getString("MSR_STUDY_NAME"));
        study.setStudyCode(rs.getString("MSR_STUDY_CODE"));
        study.setClinicalStudyId(rs.getString("MCS_STUDY_ID"));
        study.setClinicalStudyName(rs.getString("MCS_STUDY_NAME"));
        study.setRandomisation(rs.getBoolean("MSR_RANDOMISED"));
        study.setRegulatory(rs.getBoolean("MSR_REGULATORY"));
        study.setBlinding(rs.getBoolean("MSR_BLINDED"));
        study.setProjectId(rs.getLong("MSR_PRJ_ID"));
        study.setDrugProgramme(rs.getString("MPR_DRUG"));
        study.setPhase(rs.getString("MSR_PHASE"));
        try {
            study.setPhaseType(StudyRule.PhaseType.valueOf(rs.getString("MSR_PHASE_TYPE")));
        } catch (Exception e) {
            //let it be default anyway
            study.setPhaseType(StudyRule.PhaseType.EARLY);
        }
        study.setDeliveryModel(rs.getString("MSR_DELIVERY_MODEL"));
        study.setType(rs.getString("MSR_TYPE"));
        study.setFirstSubjectInPlanned(rs.getDate("MSR_FSI_PLN"));
        study.setDatabaseLockPlanned(rs.getDate("MSR_DBL_PLN"));
        study.setEnabled(true);
        study.setStatus(Status.valueOf(rs.getString("MSR_STATUS")));
        study.setStudyCompleted(rs.getInt("MSR_COMPLETED") == 1);
        study.setStudyValid(rs.getInt("MSR_VALID") == 1);
        study.setStudyEnabled(rs.getInt("MSR_ENABLED") == 1);
        study.setStudyUseAltLabCodes(rs.getInt("MSR_USE_ALT_LAB_CODES") == 1);
        study.setCronExpression(rs.getString("MSR_CRON_EXPRESSION"));
        study.setScheduled(rs.getBoolean("MSR_SCHEDULED"));
        study.setxAxisLimitedToVisit(rs.getBoolean("MSR_LIMIT_X_AXIS_TO_VISIT"));
        study.setAutoAssignedCountry(rs.getBoolean("MSR_AUTO_ASSIGNED_COUNTRY"));
        study.setPrimarySource(rs.getString("MSR_PRIMARY_SOURCE"));
        study.setUseCustomDrugsForBaseline("1".equals(rs.getString("MSR_USE_CUSTOM_BASELINE_DRUGS")));
        study.setProfilesMask(rs.getInt("MSR_CBIO_PROFILES_MASK"));
        study.setCbioPortalStudyCode(rs.getString("MSR_CBIO_PROFILE_STUDY_CODE"));
        study.setAmlEnabled(rs.getInt("MSR_AML_ENABLED") == 1);
        return study;
    };
    private final RowMapper<ETLStudyRule> etlStudyRowMapper = (rs, rowNum) -> {
        ETLStudyRule study = new ETLStudyRule();
        study.setStudyCode(rs.getString("MSR_STUDY_CODE"));
        study.setDrugName(rs.getString("mpr_drug"));
        study.setCronExpression(rs.getString("MSR_CRON_EXPRESSION"));
        study.setScheduled(rs.getBoolean("MSR_SCHEDULED"));
        study.setScheduledClean(rs.getBoolean("MSR_CLEAN_SCHEDULED"));
        study.setAmlEnabled(rs.getInt("MSR_AML_ENABLED") == 1);
        return study;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, StudyRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getStudyCode());
        ps.setString(paramIndex++, entity.getStudyName());
        ps.setString(paramIndex++, entity.getClinicalStudyId());
        ps.setBoolean(paramIndex++, entity.isBlinding());
        ps.setBoolean(paramIndex++, entity.isRegulatory());
        ps.setBoolean(paramIndex++, entity.isRandomisation());
        ps.setLong(paramIndex++, entity.getProjectId());
        ps.setString(paramIndex++, entity.getPhase());
        ps.setString(paramIndex++, entity.getPhaseType().name());
        ps.setString(paramIndex++, entity.getType());
        ps.setString(paramIndex++, entity.getDeliveryModel());
        ps.setBoolean(paramIndex++, entity.isScheduled());
        ps.setBoolean(paramIndex++, entity.isxAxisLimitedToVisit());
        ps.setBoolean(paramIndex++, entity.isAutoAssignedCountry());
        ps.setString(paramIndex++, entity.getPrimarySource());
        ps.setString(paramIndex++, entity.getCronExpression());
        ps.setDate(paramIndex++, Util.getSQLDate(entity.getFirstSubjectInPlanned()));
        ps.setDate(paramIndex++, Util.getSQLDate(entity.getDatabaseLockPlanned()));
        ps.setString(paramIndex++, entity.getStatus().toString());
        ps.setObject(paramIndex++, entity.isStudyCompleted() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyValid() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyEnabled() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyUseAltLabCodes() ? 1 : 0);
        ps.setString(paramIndex++, entity.getCreatedBy());
        ps.setDate(paramIndex++, Util.getSQLDate(entity.getCreationDate()));
        ps.setBoolean(paramIndex++, entity.isUseCustomDrugsForBaseline());
        ps.setTimestamp(paramIndex++, Util.getSQLTimestamp(entity.getMappingModifiedDate()));
        ps.setObject(paramIndex++, entity.getProfilesMask());
        ps.setString(paramIndex++, entity.getCbioPortalStudyCode());
        ps.setObject(paramIndex, entity.isAmlEnabled() ? 1 : 0);
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_STUDY_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MSR_ID").setValue("nextval('msr_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MSR_STUDY_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_STUDY_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_MCS_STUDY_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_BLINDED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_REGULATORY").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_RANDOMISED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PRJ_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PHASE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PHASE_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_DELIVERY_MODEL").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_SCHEDULED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_LIMIT_X_AXIS_TO_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_AUTO_ASSIGNED_COUNTRY").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PRIMARY_SOURCE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CRON_EXPRESSION").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_FSI_PLN").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_DBL_PLN").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_STATUS").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_COMPLETED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_VALID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_ENABLED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_USE_ALT_LAB_CODES").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CREATED_BY").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CREATION_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_USE_CUSTOM_BASELINE_DRUGS").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_MAPPING_MODIFIED_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CBIO_PROFILES_MASK").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CBIO_PROFILE_STUDY_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_AML_ENABLED").build());
        return QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
    }

    @Override
    protected String getIdColumnName() {
        return "MSR_ID";
    }

    @CacheEvict(value = "adminStudiesCache", allEntries = true)
    public void deleteByStudyCode(String studyCode) {
        getJdbcTemplate().update(STUDY_DELETE_QUERY, new Object[]{studyCode});
    }

    public StudyRule getByCode(String code) {
        List<StudyRule> reStudyRules = getJdbcTemplate().query(SEARCH_BY_STUDY_CODE_QUERY, new String[]{code}, studyRowMapper);
        if (reStudyRules.size() > 0) {
            return reStudyRules.get(0);
        }
        return null;
    }

    public List<StudyRule> getByProjectId(Long id) {
        return getJdbcTemplate().query(SEARCH_BY_PROJECT_ID_QUERY, new Object[]{id}, studyRowMapper);
    }

    public List<StudyRule> getByDrugNameAndStudyCode(String drugName, String studyCode) {
        return getJdbcTemplate().query(SEARCH_BY_PROJECT_NAME_AND_STUDY_CODE_QUERY, new Object[]{drugName, studyCode}, studyRowMapper);
    }

    public void unbind(Long projectId) {
        getJdbcTemplate().update(UNBIND_QUERY, new Object[]{projectId});
    }

    public void fillSearchQueryWorker(String query, QuerySearchWorker<StudyRule> worker) {
        query = "%" + query + "%";
        worker.setSqlACUITYQuery(SEARCH_STUDY_QUERY);
        worker.setAcuityTemplate(getJdbcTemplate());
        worker.setRowACUITYMapper(studyRowMapper);
        worker.setAcuityQueryParams(new Object[]{query, query});
    }

    public StudyRule getStudyByCode(final String code) {
        List<StudyRule> results = getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(SEARCH_BY_CODE_QUERY);
            ps.setString(1, code);
            return ps;
        }, studyRowMapper);

        if (CollectionUtils.isEmpty(results)) {
            return null;
        }

        return results.get(0);
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, StudyRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getStudyName());
        ps.setString(paramIndex++, entity.getClinicalStudyId());
        ps.setLong(paramIndex++, entity.getProjectId());
        ps.setBoolean(paramIndex++, entity.isBlinding());
        ps.setBoolean(paramIndex++, entity.isRandomisation());
        ps.setBoolean(paramIndex++, entity.isRegulatory());
        ps.setString(paramIndex++, entity.getPhase());
        ps.setString(paramIndex++, entity.getPhaseType().name());
        ps.setString(paramIndex++, entity.getType());
        ps.setString(paramIndex++, entity.getDeliveryModel());
        ps.setBoolean(paramIndex++, entity.isScheduled());
        ps.setBoolean(paramIndex++, entity.isxAxisLimitedToVisit());
        ps.setBoolean(paramIndex++, entity.isAutoAssignedCountry());
        ps.setString(paramIndex++, entity.getCronExpression());
        ps.setString(paramIndex++, entity.getPrimarySource());
        ps.setDate(paramIndex++, Util.getSQLDate(entity.getFirstSubjectInPlanned()));
        ps.setDate(paramIndex++, Util.getSQLDate(entity.getDatabaseLockPlanned()));
        ps.setString(paramIndex++, entity.getStatus().toString());
        ps.setObject(paramIndex++, entity.isStudyCompleted() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyValid() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyEnabled() ? 1 : 0);
        ps.setObject(paramIndex++, entity.isStudyUseAltLabCodes() ? 1 : 0);
        ps.setBoolean(paramIndex++, entity.isUseCustomDrugsForBaseline());
        ps.setTimestamp(paramIndex++, Util.getSQLTimestamp(entity.getMappingModifiedDate()));
        ps.setObject(paramIndex++, entity.getProfilesMask());
        ps.setString(paramIndex++, entity.getCbioPortalStudyCode());
        ps.setObject(paramIndex++, entity.isAmlEnabled() ? 1 : 0);
        ps.setLong(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = "MAP_STUDY_RULE";
        List<TableField> fieldsToInsert = new ArrayList<>();
        List<TableField> whereFields = new ArrayList<>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MSR_STUDY_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_MCS_STUDY_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PRJ_ID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_BLINDED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_RANDOMISED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_REGULATORY").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PHASE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PHASE_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_DELIVERY_MODEL").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_SCHEDULED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_LIMIT_X_AXIS_TO_VISIT").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_AUTO_ASSIGNED_COUNTRY").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CRON_EXPRESSION").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_PRIMARY_SOURCE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_FSI_PLN").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_DBL_PLN").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_STATUS").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_COMPLETED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_VALID").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_ENABLED").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_USE_ALT_LAB_CODES").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_USE_CUSTOM_BASELINE_DRUGS").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_MAPPING_MODIFIED_DATE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CBIO_PROFILES_MASK").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_CBIO_PROFILE_STUDY_CODE").build());
        fieldsToInsert.add(fieldBuilder.setField("MSR_AML_ENABLED").build());
        whereFields.add(fieldBuilder.setField("MSR_ID").build());
        return QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
    }

    @CacheEvict(value = "adminStudiesCache", allEntries = true)
    public void removeStudy(final Long studyId) {
        getJdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement("delete from MAP_STUDY_RULE where msr_id = ?");
            ps.setLong(1, studyId);
            return ps;
        });
    }

    public StudyRule getStudyById(long studyId) {
        final String sql = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
                + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) where msr_id=?";
        return getJdbcTemplate().queryForObject(sql, studyRowMapper, studyId);
    }

    public List<StudyRule> getStudies(final List<Long> ids) {
        List<StudyRule> results = getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement("select *"
                    + " from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID:"
                    + "  INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) where msr_id in (" + DaoUtils.generateQuestionMarks(ids.size()) + ")");
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }
            return ps;
        }, studyRowMapper);

        if (CollectionUtils.isEmpty(results)) {
            return null;
        }

        return results;
    }

    @CacheEvict(value = "adminStudiesCache", allEntries = true)
    public Long insert(StudyRule entity) {
        entity.setCreationDate(new Date());
        entity.setMappingModifiedDate(new Date());
        return super.insert(entity);
    }

    @CacheEvict(value = "adminStudiesCache", allEntries = true)
    public void update(StudyRule entity) {
        entity.setMappingModifiedDate(new Date());
        super.update(entity);
    }

    @Cacheable(value = "adminStudiesCache", key = "'allStudyRules'")
    public List<StudyRule> getAllStudyRules() {
        logger.debug("getAllStudyRules");
        String sql = "SELECT * FROM map_study_rule LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID"
            + " INNER JOIN map_project_rule ON msr_prj_id=mpr_id ORDER BY MSR_STUDY_CODE";
        return getJdbcTemplate().query(sql, studyRowMapper);
    }

    public List<ETLStudyRule> getEtlConfigList() {
        return getJdbcTemplate().query(SEARCH_FOR_ETL_EXECUTE_QUERY, etlStudyRowMapper);
    }

    static String createSqlSelectStudyRules(List<StudyType> studyTypes) {
        StringBuilder sql = new StringBuilder("select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY"
                + " ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID)"
                + " WHERE MSR_PRJ_ID = ?");

        boolean or = false;
        for (StudyType studyType : studyTypes) {

            if (studyType.hasNotNull()) {
                boolean and = false;

                if (or) {
                    sql.append(" or ");
                } else {
                    sql.append(" and (");
                }

                sql.append("(");
                if (studyType.getBlinded() != null) {
                    sql.append("MSR_BLINDED=").append(studyType.getBlinded());
                    and = true;
                }

                if (studyType.getRandomised() != null) {
                    if (and) {
                        sql.append(" and ");
                    }
                    sql.append("MSR_RANDOMISED=").append(studyType.getRandomised());
                    and = true;
                }

                if (studyType.getRegulatory() != null) {
                    if (and) {
                        sql.append(" and ");
                    }
                    sql.append("MSR_REGULATORY=").append(studyType.getRegulatory());
                }
                sql.append(")");
                or = true;
            }
        }
        if (or) {
            sql.append(")");
        }
        return sql.toString();
    }

    public List<String> getStudyCodes() {
        String sql = "select msr_study_code from map_study_rule";
        return getJdbcTemplate().queryForList(sql, String.class);
    }

    public List<String> getStudyCodesByDrug(String drug) {
        String sql = "select msr_study_code from map_study_rule, map_project_rule where msr_prj_id=mpr_id and mpr_drug=?";
        return getJdbcTemplate().queryForList(sql, String.class, drug.toUpperCase());
    }

}

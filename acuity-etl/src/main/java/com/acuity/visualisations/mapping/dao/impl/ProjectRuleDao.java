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
import com.acuity.visualisations.mapping.AeSeverityType;
import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.dao.QuerySearchWorker;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ProjectRuleDao extends BasicDynamicEntityDao<ProjectRule> implements IProjectRuleDao {
    private static final String SEARCH_PROJECTS_BY_DRUGS_QUERY
            = "select MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED,"
            + " sum(nvl2(msr_id, 1, 0)) as NBR_RCT_STUDIES from MAP_STUDY_RULE"
            + " right join MAP_PROJECT_RULE on MSR_PRJ_ID = MPR_ID and MPR_DRUG in ";

    private static final String SEARCH_PROJECTS_BY_DRUG_QUERY
            = "select MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED,"
            + " sum(nvl2(msr_id, 1, 0)) as NBR_RCT_STUDIES from MAP_STUDY_RULE"
            + " right join MAP_PROJECT_RULE on MSR_PRJ_ID = MPR_ID"
            + " WHERE upper(MPR_DRUG) = upper(?)"
            + " group by MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED ";

    private static final String SELECT_PROJECT_COUNT_QUERY
            = "select count(1) as PROJECT_COUNT from MAP_PROJECT_RULE where upper(MPR_DRUG) = upper(?)";

    private static final String SEARCH_PROJECT_BY_ID_QUERY
            = "select MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED,"
            + " sum(nvl2(msr_id, 1, 0)) as NBR_RCT_STUDIES from MAP_STUDY_RULE"
            + " right join MAP_PROJECT_RULE on MSR_PRJ_ID = MPR_ID"
            + " WHERE MPR_ID = ?"
            + " group by MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED";

    private static final String SEARCH_QUERY
            = "select MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED,"
            + " sum(nvl2(msr_id, 1, 0)) as NBR_RCT_STUDIES from MAP_STUDY_RULE"
            + " right join MAP_PROJECT_RULE on MSR_PRJ_ID = MPR_ID"
            + " WHERE (LOWER(MPR_DRUG) LIKE ? OR LOWER(MSR_STUDY_CODE) LIKE ? )"
            + " group by MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED"
            + " order by MPR_DRUG";

    private static final String SEARCH_COMPLETED_QUERY
            = "select MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED,"
            + " sum(nvl2(msr_id, 1, 0)) as NBR_RCT_STUDIES from MAP_STUDY_RULE"
            + " right join MAP_PROJECT_RULE on MSR_PRJ_ID = MPR_ID"
            + " WHERE MPR_COMPLETED = 1"
            + " group by MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED"
            + " order by MPR_DRUG";
    private static final RowMapper<ProjectRule> ROW_MAPPER = (rs, rowNum) -> {
        ProjectRule prj = new ProjectRule();
        prj.setId(rs.getLong("MPR_ID"));
        prj.setDrugId(rs.getString("MPR_DRUG"));
        prj.setDrugProgrammeName(rs.getString("MPR_DRUG_DISPLAY_NAME"));
        prj.setCompleted(rs.getInt("MPR_COMPLETED"));
        prj.setAcuityEnabled(true);
        prj.setNumberOfAcuityEnabledStudies(rs.getInt("NBR_RCT_STUDIES"));
        prj.setAeSeverityType(AeSeverityType.valueOf(rs.getString("MPR_AE_SEVERITY_TYPE")));
        return prj;
    };

    @Override
    protected void prepareStatementToInsert(PreparedStatement ps, ProjectRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getDrugId());
        ps.setString(paramIndex++, entity.getDrugProgrammeName());
        ps.setObject(paramIndex++, entity.isCreateDashboard() ? 1 : 0);
        ps.setString(paramIndex++, entity.getAeSeverityType() == null ? AeSeverityType.CTC_GRADES.name() : entity.getAeSeverityType().name());
        ps.setString(paramIndex++, entity.getCreatedBy());
        ps.setDate(paramIndex, Util.getSQLDate(entity.getCreationDate()));
    }

    @Override
    protected String getInsertStatement() {
        String targetTable = "MAP_PROJECT_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MPR_ID").setValue("nextval('mpr_seq')").build());
        fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MPR_DRUG").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_DRUG_DISPLAY_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_CREATE_DASHBOARD").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_AE_SEVERITY_TYPE").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_CREATED_BY").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_CREATION_DATE").build());
        String sql = QueryBuilderUtil.buildInsertQuery(targetTable, fieldsToInsert);
        return sql;
    }

    @Override
    protected void prepareStatementToUpdate(PreparedStatement ps, ProjectRule entity) throws SQLException {
        int paramIndex = 1;
        ps.setString(paramIndex++, entity.getDrugId());
        ps.setString(paramIndex++, entity.getDrugProgrammeName());
        ps.setObject(paramIndex++, entity.isCreateDashboard() ? 1 : 0);
        ps.setString(paramIndex++, entity.getAeSeverityType().name());
        ps.setLong(paramIndex, entity.getId());
    }

    @Override
    protected String getUpdateStatement() {
        String targetTable = "MAP_PROJECT_RULE";
        List<TableField> fieldsToInsert = new ArrayList<TableField>();
        List<TableField> whereFields = new ArrayList<TableField>();
        TableFieldBuilder fieldBuilder = new TableFieldBuilder(targetTable);
        fieldsToInsert.add(fieldBuilder.setField("MPR_DRUG").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_DRUG_DISPLAY_NAME").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_CREATE_DASHBOARD").build());
        fieldsToInsert.add(fieldBuilder.setField("MPR_AE_SEVERITY_TYPE").build());
        whereFields.add(fieldBuilder.setField("MPR_ID").build());
        String sql = QueryBuilderUtil.buildUpdateQuery(targetTable, fieldsToInsert, whereFields);
        return sql;
    }

    @Override
    protected String getIdColumnName() {
        return "MPR_ID";
    }

    public List<ProjectRule> searchByDrugs(Collection<String> drugIds) {
        if (CollectionUtils.isEmpty(drugIds)) {
            return Collections.emptyList();
        }
        String queryPart = '(' + StringUtils.repeat("?,", drugIds.size() - 1) + "?" + ')';
        String groupByClause = " group by MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_CREATE_DASHBOARD, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED";
        return getJdbcTemplate().query(SEARCH_PROJECTS_BY_DRUGS_QUERY + queryPart + groupByClause, drugIds.toArray(new String[drugIds.size()]), ROW_MAPPER);
    }

    public ProjectRule getProjectByDrug(String drugId) {
        List<ProjectRule> result = getJdbcTemplate().query(SEARCH_PROJECTS_BY_DRUG_QUERY, new Object[]{drugId}, ROW_MAPPER);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    public boolean isProjectExistByDrug(String drugId) {
        int result = getJdbcTemplate().queryForObject(SELECT_PROJECT_COUNT_QUERY, Integer.class, drugId);
        return result > 0;
    }

    public void fillSearchQueryWorker(String query, QuerySearchWorker<ProjectRule> worker) {
        worker.setSqlACUITYQuery(SEARCH_QUERY);
        worker.setAcuityTemplate(getJdbcTemplate());
        worker.setRowACUITYMapper(ROW_MAPPER);
        worker.setAcuityQueryParams(new Object[]{query.toLowerCase(), query.toLowerCase()});
    }

    public List<ProjectRule> search(String query) {
        return getJdbcTemplate().query(SEARCH_QUERY, new Object[]{query.toLowerCase(), query.toLowerCase()}, ROW_MAPPER);
    }

    public List<ProjectRule> getCompletedProjects() {
        return getJdbcTemplate().query(SEARCH_COMPLETED_QUERY, ROW_MAPPER);
    }

    public ProjectRule getProjectById(long projectId) {
        List<ProjectRule> result = getJdbcTemplate().query(SEARCH_PROJECT_BY_ID_QUERY, ROW_MAPPER, projectId);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    @CacheEvict(value = "adminProjectsCache", allEntries = true)
    public Long insert(ProjectRule entity) {
        return super.insert(entity);
    }

    @CacheEvict(value = "adminProjectsCache", allEntries = true)
    public void update(ProjectRule entity) {
        super.update(entity);
    }

    @CacheEvict(value = "adminProjectsCache", allEntries = true)
    public void removeProject(final Long projectId) {
        getJdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement("delete from map_project_rule where mpr_id = ?");
            ps.setLong(1, projectId);
            return ps;
        });
    }

    @CacheEvict(value = "adminProjectsCache", allEntries = true)
    public void setProjectCompleted(final Long id) {
        getJdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement("update  map_project_rule set MPR_COMPLETED=1 where mpr_id = ?");
            ps.setLong(1, id);
            return ps;
        });
    }

    public Integer listedInACUITYCount(final List<String> drugIds) {
        Object[] args = new Object[drugIds.size()];
        for (int i = 0; i < drugIds.size(); i++) {
            args[i] = drugIds.get(i);
        }
        String query = "select count(1) from map_project_rule"
                + (drugIds.size() > 0 ? " where MPR_DRUG not in (" + DaoUtils.generateQuestionMarks(drugIds.size()) + ")" : "");
        return getJdbcTemplate().queryForObject(query, args, Integer.class);
    }

    @Cacheable(value = "adminProjectsCache", key = "'allProjectRules'")
    public List<ProjectRule> getAllProjectRules() {
        logger.debug("getAllProjectRules");
        return getJdbcTemplate().query(
                "SELECT MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED, sum(nvl2(msr_id, 1, 0)) AS NBR_RCT_STUDIES "
                        + "FROM  MAP_PROJECT_RULE LEFT JOIN MAP_STUDY_RULE ON MSR_PRJ_ID= MPR_ID "
                        + "GROUP BY MPR_ID, MPR_DRUG, MPR_DRUG_DISPLAY_NAME, MPR_AE_SEVERITY_TYPE, MPR_COMPLETED ORDER BY MPR_DRUG",
                ROW_MAPPER);
    }
}

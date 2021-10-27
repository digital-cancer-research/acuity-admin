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

import com.acuity.visualisations.mapping.dao.ISubjectGroupRuleDao;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.SubjectGroupRule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubjectGroupRuleDao extends ProjectGroupRuleDaoBase implements ISubjectGroupRuleDao {
    private static final String GROUP_DELETE_QUERY = "delete from MAP_SUBJECT_GROUP_RULE where MSGR_ID=?";
    private static final String GET_BY_PROJECT_QUERY = "select * from MAP_SUBJECT_GROUP_RULE where MSGR_STUDY_ID = ?";

    private static final String DELETE_SUBJECT_GROUPING_ANNOTATION =
            "DELETE FROM map_subject_grouping"
                    + " WHERE (msg_study_id, msg_grouping_name) IN"
                    + " (SELECT msgr_study_id, msgr_name FROM map_subject_group_rule WHERE msgr_id=?)";

    private static final String NO_RELATION_TABLE_MSG = "There is no relation table for subject groups";

    @Override
    protected String getTableName() {
        return "MAP_SUBJECT_GROUP_RULE";
    }

    @Override
    protected String getRelationTableName() {
        throw new UnsupportedOperationException(NO_RELATION_TABLE_MSG);
    }

    @Override
    protected String getGroupRelationId() {
        throw new UnsupportedOperationException(NO_RELATION_TABLE_MSG);
    }

    @Override
    protected String getRelationId() {
        throw new UnsupportedOperationException(NO_RELATION_TABLE_MSG);
    }

    @Override
    protected GroupRuleBase createInstance() {
        return new SubjectGroupRule();
    }

    @Override
    protected String getIdColumnName() {
        return "MSGR_ID";
    }

    @Override
    protected String getPrefix() {
        return "MSGR";
    }

    @Override
    protected String getParentColumnName() {
        return "MSGR_STUDY_ID";
    }

    public void delete(GroupRuleBase group) {
        getJdbcTemplate().update(DELETE_SUBJECT_GROUPING_ANNOTATION, group.getId());
        getJdbcTemplate().update(GROUP_DELETE_QUERY, group.getId());
    }

    public List<SubjectGroupRule> getByStudyId(Long id) {
        return getJdbcTemplate().query(GET_BY_PROJECT_QUERY, new Object[]{id}, (rs, rowNum) -> {
            SubjectGroupRule result = new SubjectGroupRule();
            result.setId(rs.getLong("MSGR_ID"));
            result.setName(rs.getString("MSGR_NAME"));
            result.setDefaultValue(rs.getString("MSGR_DEFAULT_VALUE"));
            result.setTime(rs.getString("MSGR_TIME"));
            result.setParentId(rs.getLong("MSGR_STUDY_ID"));
            result.setReady((rs.getInt("MSGR_ENABLED") == 1) ? true : false);
            return result;
        });
    }

    public List<String> getGroupingNamesFromSubjectTable(long studyRuleId) {
        String sql = "SELECT DISTINCT pgr_grouping_name "
                + "FROM result_patient_group "
                + "JOIN result_study ON std_id=pgr_std_id "
                + "JOIN map_study_rule ON msr_study_code=std_name "
                + "WHERE msr_id=?";
        return getJdbcTemplate().queryForList(sql, String.class, studyRuleId);
    }
}

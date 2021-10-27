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

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IRelationDao;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudySubjectGrouping;
import com.acuity.visualisations.mapping.entity.SubjectGrouping;
import com.acuity.visualisations.util.Pair;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@PropertySource("classpath:/com/acuity/visualisations/mapping/dao/impl/RelationDao.xml")
public class RelationDao extends ACUITYDaoSupport implements IRelationDao {
    private static final String INSERT_FILE_DESCRIPTION_RELATION_QUERY
            = "insert into MAP_DESCRIPTION_FILE (MDF_ID, MDF_MFR_ID, MDF_MFD_ID) values (nextval('mdf_seq'), ?, ?)";

    private static final String DELETE_SUBJECT_GROUPING_BY_STUDY_AND_GROUPING_NAME =
            "DELETE FROM MAP_SUBJECT_GROUPING "
                    + "WHERE MSG_ID IN  ( "
                    + "   SELECT MSG_ID "
                    + "   FROM MAP_SUBJECT_GROUPING "
                    + "   WHERE MSG_STUDY_ID = ? AND MSG_GROUPING_NAME LIKE ? "
                    + ")";

    private static final String INSERT_STUDY_AE_GROUPS_QUERY
            = "insert into MAP_STUDY_AE_GROUP (MSAG_ID ,MSAG_AE_GROUP_ID ,MSAG_MSR_ID ) values(nextval('msag_seq'),?,?)";
    private static final String DELETE_STUDY_AE_GROUPS_QUERY = "delete from MAP_STUDY_AE_GROUP where MSAG_MSR_ID =?";

    private static final String INSERT_STUDY_LAB_GROUPS_QUERY
            = "insert into MAP_STUDY_LAB_GROUP (MSLG_ID ,MSLG_LAB_GROUP_ID ,MSLG_MSR_ID ) values(nextval('mslg_seq'),?,?)";
    private static final String DELETE_STUDY_LAB_GROUPS_QUERY = "delete from MAP_STUDY_LAB_GROUP where MSLG_MSR_ID =?";

    private static final String INSERT_MAPPING_RULE_FIELDS_QUERY
            = "insert into MAP_MAPPING_RULE_FIELD (MRF_ID ,MRF_MMR_ID ,MRF_MFI_ID ) values(nextval('mrf_seq'),?,?)";
    private static final String DELETE_MAPPING_RULE_FIELDS_QUERY = "delete from MAP_MAPPING_RULE_FIELD where MRF_MMR_ID = ?";

    private static final String DELETE_MAP_DESCRIPTION_FILE = "delete from MAP_DESCRIPTION_FILE where MDF_MFR_ID = ?";

    private static final String SELECTED_CONDITION_TOKEN = "%%SELECTED_CONDITION%%";
    private static final String SELECTED_CONDITION = " and (msg_grouping_name, msgt_type) in (%%GRP_TYPE_PAIRS_TOKEN%%) ";
    private static final String FALSE_CONDITION = " and 1=0 ";
    private static final String GRP_TYPE_PAIRS_TOKEN = "%%GRP_TYPE_PAIRS_TOKEN%%";
    private static final RowMapper<Pair<Long, Long>> ROW_MAPPER = (rs, rowNum) -> new Pair<>(rs.getLong(1), rs.getLong(2));
    private static final RowMapper<SubjectGrouping> SUBJECT_GROUPING_MAPPER = (rs, rowNum) -> {
        SubjectGrouping out = new SubjectGrouping();
        out.setName(rs.getString("grouping_name"));
        out.setType(rs.getString("grouping_type"));
        return out;
    };
    @Value("${SELECT_ALL_STUDY_GROUPINGS}")
    private String SELECT_ALL_STUDY_GROUPINGS;
    @Value("${SELECT_SELECTED_STUDY_GROUPINGS}")
    private String SELECT_SELECTED_STUDY_GROUPINGS;
    @Value("${UPDATE_SELECTED_STUDY_GROUPINGS}")
    private String UPDATE_SELECTED_STUDY_GROUPINGS;

    public void insertFileDescriptionRelations(FileRule fileRule) {
        for (FileDescription desc : fileRule.getDescriptions()) {
            getJdbcTemplate().update(INSERT_FILE_DESCRIPTION_RELATION_QUERY, fileRule.getId(), desc.getId());
        }
    }

    @Override
    public int[] deleteSubjectGroupingByStudyAndName(@NonNull Long studyId, @NonNull Stream<String> names) {
        return getJdbcTemplate()
                .batchUpdate(
                        DELETE_SUBJECT_GROUPING_BY_STUDY_AND_GROUPING_NAME,
                        names
                                .map(name -> new Object[]{studyId, name})
                                .collect(Collectors.toList())
                );
    }

    @Override
    public void deleteStudyAeGroups(StudyRule study) {
        getJdbcTemplate().update(DELETE_STUDY_AE_GROUPS_QUERY, new Object[]{study.getId()});
    }

    @Override
    public void deleteStudyLabGroups(StudyRule study) {
        getJdbcTemplate().update(DELETE_STUDY_LAB_GROUPS_QUERY, new Object[]{study.getId()});
    }

    @Override
    public void insertStudyAeGroups(StudyRule study) {
        for (GroupRuleBase group : study.getAeGroups()) {
            getJdbcTemplate().update(INSERT_STUDY_AE_GROUPS_QUERY, new Object[]{group.getId(), study.getId()});
        }

    }

    @Override
    public void insertStudyLabGroups(StudyRule study) {
        for (GroupRuleBase group : study.getLabGroups()) {
            getJdbcTemplate().update(INSERT_STUDY_LAB_GROUPS_QUERY, new Object[]{group.getId(), study.getId()});
        }
    }

    public void insertMappingFields(MappingRule rule) {
        for (FieldRule field : rule.getFieldRules()) {
            getJdbcTemplate().update(INSERT_MAPPING_RULE_FIELDS_QUERY, new Object[]{rule.getId(), field.getId()});
        }
    }

    public void deleteMappingFields(MappingRule rule) {
        getJdbcTemplate().update(DELETE_MAPPING_RULE_FIELDS_QUERY, new Object[]{rule.getId()});
    }

    public List<Pair<Long, Long>> getFileRuleRelFileDescrByStudy(long studyId) {
        String sql = "select MFR_ID, MDF_MFD_ID from MAP_FILE_RULE"
                + " inner join MAP_DESCRIPTION_FILE on MFR_ID=MDF_MFR_ID"
                + " where MFR_MSR_ID = ?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyId);
    }

    @Override
    public List<Long> getFileDescriptionIdsForFileRule(long fileRuleId) {
        String sql = "select MDF_MFD_ID from MAP_DESCRIPTION_FILE where MDF_MFR_ID=?";
        return getJdbcTemplate().queryForList(sql, Long.class, fileRuleId);
    }

    public List<Pair<Long, Long>> getMappingRuleRelFieldByStudy(long studyId) {
        String sql = "select MRF_MMR_ID, MRF_MFI_ID from MAP_MAPPING_RULE_FIELD"
                + " inner join MAP_MAPPING_RULE on MRF_MMR_ID=MMR_ID"
                + " inner join MAP_FILE_RULE on MMR_MFR_ID=MFR_ID"
                + " where MFR_MSR_ID=?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, studyId);
    }

    @Override
    public List<Pair<Long, Long>> getMappingRuleRelFieldForFileRule(long fileRuleId) {
        String sql = "select MRF_MMR_ID, MRF_MFI_ID from MAP_MAPPING_RULE_FIELD"
                + " inner join MAP_MAPPING_RULE on MRF_MMR_ID=MMR_ID"
                + " where MMR_MFR_ID=?";
        return getJdbcTemplate().query(sql, ROW_MAPPER, fileRuleId);

    }

    public void deleteMapFileDescription(final Long fileRuleId) {
        getJdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement(DELETE_MAP_DESCRIPTION_FILE);
            ps.setObject(1, fileRuleId);
            return ps;
        });
    }

    @Override
    public StudySubjectGrouping getAllStudySubjectGroupings(long studyId) {

        StudySubjectGrouping studySubjectGrouping = new StudySubjectGrouping();
        studySubjectGrouping.setStudyId(studyId);

        List<SubjectGrouping> groupings = getJdbcTemplate().query(
                SELECT_ALL_STUDY_GROUPINGS + " ORDER BY grouping_name",
                SUBJECT_GROUPING_MAPPER, studyId);

        studySubjectGrouping.addGroupings(groupings);
        return studySubjectGrouping;
    }

    @Override
    public StudySubjectGrouping getSelectedStudySubjectGroupings(long studyId) {

        StudySubjectGrouping studySubjectGrouping = new StudySubjectGrouping();
        studySubjectGrouping.setStudyId(studyId);

        List<SubjectGrouping> groupings = getJdbcTemplate().query(
                SELECT_SELECTED_STUDY_GROUPINGS + " ORDER BY grouping_name",
                SUBJECT_GROUPING_MAPPER, studyId);

        studySubjectGrouping.addGroupings(groupings);
        return studySubjectGrouping;
    }

    @Override
    public void saveSelectedStudySubjectGroupings(StudySubjectGrouping grouping) {
        String selectedCondition = FALSE_CONDITION;
        ArrayList sqlParams = new ArrayList();
        sqlParams.add(grouping.getStudyId());
        List<Pair> params = grouping.getGroupingsType().entrySet().stream().filter(e -> e.getValue() != null)
                .flatMap(e -> e.getValue().stream().filter(v -> v != null)
                        .map(v -> new Pair(e.getKey(), v.getName()))).collect(Collectors.toList());
        if (!params.isEmpty()) {
            StringJoiner joiner = new StringJoiner(",");
            params.forEach(p -> {
                joiner.add("(?, ?)");
                sqlParams.add(p.getB());
                sqlParams.add(p.getA());
            });
            selectedCondition = SELECTED_CONDITION.replace(GRP_TYPE_PAIRS_TOKEN, joiner.toString());
        }

        sqlParams.add(grouping.getStudyId());
        String sql = UPDATE_SELECTED_STUDY_GROUPINGS.replace(SELECTED_CONDITION_TOKEN, selectedCondition);
        getJdbcTemplate().update(sql, sqlParams.toArray());
    }
}

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

package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroup;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupDosing;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupDosingSchedule;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGrouping;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupingType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StudySetupSubjectGroupRepository {

    @Select("SELECT msgt_id, msgt_type FROM MAP_SUBJECT_GROUPING_TYPE")
    @Results({
            @Result(property = "id", column = "msgt_id", id = true),
            @Result(property = "name", column = "msgt_type"),
    })
    List<SubjectGroupingType> selectSubjectGroupingTypes();

    /**
     * Select all Subject Groups for Study
     *
     * @param studyId
     * @return
     */
    List<SubjectGroup> selectSubjectGroups(@Param("studyId") long studyId);

    /**
     * Select saved Subject Groupings for Study
     *
     * @param studyId
     * @return
     */
    List<SubjectGrouping> selectSavedSubjectGroupings(@Param("studyId") long studyId);

    SubjectGrouping selectSavedSubjectGroupingByName(@Param("studyId") long studyId, @Param("groupingName") String groupingName);

    /**
     * Select single Subject Group by Group Id
     *
     * @param groupId
     * @return
     */
    SubjectGroup selectSubjectGroup(@Param("subjectGroupId") long groupId);

    /**
     * Insert Subject Group
     *
     * @param groupingId
     * @param subjectGroup
     */
    void insertSubjectGroup(@Param("subjectGroupingId") long groupingId, @Param("subjectGroup") SubjectGroup subjectGroup);

    /**
     * Insert Subject Group Dosing
     *
     * @param subjectGroupId
     * @param subjectGroupDosing
     */
    void insertSubjectGroupDosing(@Param("subjectGroupId") long subjectGroupId,
                                  @Param("subjectGroupDosing") SubjectGroupDosing subjectGroupDosing);

    /**
     * Insert Subject Group Dosing Schedule
     *
     * @param subjectGroupDosingId
     * @param subjectGroupDosingSchedule
     */
    void insertSubjectGroupDosingSchedule(@Param("subjectGroupDosingId") long subjectGroupDosingId,
                                          @Param("subjectGroupDosingSchedule") SubjectGroupDosingSchedule subjectGroupDosingSchedule);

    /**
     * Update Subject Group
     *
     * @param subjectGroup
     */
    void updateSubjectGroup(@Param("subjectGroup") SubjectGroup subjectGroup);


    /**
     * Delete Subject Group
     *
     * @param subjectGroupId
     */
    void deleteSubjectGroup(@Param("subjectGroupId") long subjectGroupId);

    /**
     * Delete cascade Subject Group Dosings and Subject Group Dosing Schedule
     *
     * @param subjectGroupId
     */
    void deleteSubjectGroupDosings(@Param("subjectGroupId") long subjectGroupId);

    void insertSubjectGrouping(@Param("studyId") long studyId, @Param("subjectGrouping") SubjectGrouping subjectGrouping);

    void updateSubjectGrouping(@Param("subjectGrouping") SubjectGrouping subjectGrouping);
}

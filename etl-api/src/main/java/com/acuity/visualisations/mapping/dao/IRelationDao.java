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

package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudySubjectGrouping;
import com.acuity.visualisations.util.Pair;

import java.util.List;
import java.util.stream.Stream;

public interface IRelationDao {

    void insertFileDescriptionRelations(FileRule fileRule);

    /**
     * Removes groupings from study by its names.
     * Affected tables: MAP_SUBJECT_GROUPING (refers to custom groupings)
     *
     * @param studyId study to delete from
     * @param names   grouping names to delete
     * @return deletion result (represents success(1)/failure(0) results one-by-one)
     */
    int[] deleteSubjectGroupingByStudyAndName(Long studyId, Stream<String> names);

    void deleteStudyAeGroups(StudyRule study);

    void deleteStudyLabGroups(StudyRule study);

    void insertStudyAeGroups(StudyRule study);

    void insertStudyLabGroups(StudyRule study);

    void insertMappingFields(MappingRule rule);

    void deleteMappingFields(MappingRule rule);

    List<Pair<Long, Long>> getFileRuleRelFileDescrByStudy(long studyId);

    List<Pair<Long, Long>> getMappingRuleRelFieldByStudy(long studyId);

    void deleteMapFileDescription(Long fileRuleId);

    List<Pair<Long, Long>> getMappingRuleRelFieldForFileRule(long fileRuleId);

    List<Long> getFileDescriptionIdsForFileRule(long fileRuleId);

    StudySubjectGrouping getAllStudySubjectGroupings(long studyId);

    StudySubjectGrouping getSelectedStudySubjectGroupings(long studyId);

    void saveSelectedStudySubjectGroupings(StudySubjectGrouping grouping);
}

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

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.mapping.entity.AggregationFunction;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileSection;
import com.acuity.visualisations.mapping.entity.FileType;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;

import java.util.List;

public interface IStudyMappingsServicePartial {

	void getExistingFileRules(StudyRule study, boolean loadMappings);

    /**
     * Removes mapping rule and corresponding groupings, related to the mapping rule
     *
     * @param rule mapping rule to delete
     */
    void deleteMappingRule(MappingRule rule);

    /**
     * Removes fileRule related mapping rules (one-by-one, calling {@link #deleteMappingRule(MappingRule)} )
     *
     * @param fileRule file rule to delete mappings from
     */
    void deleteMappingRules(FileRule fileRule);

	List<FileDescription> getFileDescriptions();

	List<AggregationFunction> getAggregationFunctions();

	List<FieldRule> getFields();

	List<FileType> getFileTypes();

	AggregationFunction getDefaultAggregationFunction();

	void saveMappingRules(FileRule fileRule);

	void checkValidStatus(StudyRule study);

    /**
     * Removes file rule, its description, mapping rules (one-by-one, calling {@link #deleteMappingRule(MappingRule)} )
     *
     * @param fileRule file rule to delete
     */
    void deleteFileRule(FileRule fileRule, StudyRule study);

	List<FileSection> getFileSections();

	void generateFileMappings(FileRule fileRule);

	void saveFileRule(FileRule fileRule);

	void validateStudyCompleted(StudyRule study, List<FileSection> sections);

	List<FileRule> validateMappings(StudyRule study);

	void validateStudyEnabled(StudyRule study);

    FileRule getFileRule(long similarFileRuleId);

	FieldRule saveDynamicFieldRule(MappingRule mappingRule, String name);

	FieldRule getDynamicFieldRuleByMappingRule(MappingRule mappingRule);

	void deleteDynamicFieldRuleByMappingRule(MappingRule mappingRule);
}

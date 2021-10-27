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

package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.table.ForeignKeyRule;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigurationUtil<T extends StudyMappingConfigurationHolder> {

    void setMappingConfigurationHolder(T mappingConfigurationHolder);

    void setStaticConfigurationHolder(StaticConfigurationHolder staticConfigurationHolder);

    IStudyRule getStudy();

    List<String> getEntities(String dataFileName);

    List<? extends IColumnRule> getColumns(String dataFileName, String entityName);

    List<String> getNotCumulativeEntities();

    String[] getDataFileNames();

    List<String> getDataFileNames(String entityName);

    List<String> getColumnNames(String fileName);

    String getEntityNameByColName(String fileName, String colName);

    String getFieldNameByColName(String fileName, String colName);

    Map<String, String> getFilters(String fileName);

    Map<Integer, String> getEmptyColumnsDefaultValues(String fileName);

    Map<Integer, String> getEmptyColumnsDefaultValues(String fileName, Set<String> columnsToSkip);

    Map<Integer, String> getDefaultValues(String fileName);

    Map<Integer, String> getDefaultValues(String fileName, Set<String> columnsToSkip);

    List<String> getEntityNames();

    Map<String, Map<String, List<String>>> getColumns(String entityName);

    boolean isUnique(String sasColumnName, String dataFileName);

    IColumnRule getStudyColumn(String fileName, String entityName);

    Map<String, String> getFKfields(OutputEntity childEntity, ForeignKeyRule fkRule);

    String[] getReferFieldNames(OutputEntity entity, ForeignKeyRule fkRule);

    String[] getParentEntity(ForeignKeyRule foreignKeyRule);

    String[] getParentEntity(String childEntity);

    List<String> getParentEntities();

    List<DataFileRule> getCntlinFileRules();

    void addSkippedEntities(List<String> entityNames);

    List<String> getSkippedEntities();
}

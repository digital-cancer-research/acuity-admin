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

import com.acuity.visualisations.exception.MethodFailedException;
import com.acuity.visualisations.mapping.dao.IEntityRuleDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.ExcludingValue;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.transform.rule.ColumnRule;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.EntityRule;
import com.acuity.visualisations.transform.rule.FileType;
import com.acuity.visualisations.transform.rule.ParserRule;
import com.acuity.visualisations.transform.rule.ProjectRule;
import com.acuity.visualisations.transform.rule.StudyRule;
import com.acuity.visualisations.web.service.IStudyMappingsServicePartial;
import com.acuity.visualisations.web.service.wizard.study.StudyExcludingValueServicePartial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DBStudyMappingConfigurationHolder implements StudyMappingConfigurationHolder {

    private static final Long CNTLIN_FILE_DESCRIPTION_ID = 18L;
    private static final Long SUBJECT_FIELD_DESCRIPTION_ID = 2L;
    private static final String NO_AGGREGATION_TERM = "noAggregation";

    @Autowired
    private IStudyRuleDao studyRuleDao;
    @Autowired
    private IEntityRuleDao entityRuleDao;
    @Autowired
    private IStudyMappingsServicePartial studyMappingsServicePartial;
    @Autowired
    private StudyExcludingValueServicePartial excludingValueService;

    private String projectName;
    private String studyCode;
    private ProjectRule projectRule;
    private List<DataFileRule> cntlinFileRules;

    @Override
    public ConfigurationUtil<?> createConfigurationUtil() {
        DBConfigurationUtil dbConfigurationUtil = new DBConfigurationUtil();
        dbConfigurationUtil.setMappingConfigurationHolder(this);
        return dbConfigurationUtil;
    }

    public List<DataFileRule> getCntlinFileRules() {
        return cntlinFileRules;
    }

    public ProjectRule getProjectRule() {
        return projectRule;
    }

    public void loadConfiguration(String projectName, String studyCode) throws MethodFailedException {
        this.projectName = projectName;
        this.studyCode = studyCode;
        projectRule = new ProjectRule();
        projectRule.setName(projectName);
        cntlinFileRules = null;
        projectRule.setStudy(loadStudy());
    }

    private static Map<String, com.acuity.visualisations.mapping.entity.EntityRule> allEntityRules;

    private void loadAllEntityRules() {
        if (allEntityRules == null) {
            allEntityRules = new HashMap<>();
            for (com.acuity.visualisations.mapping.entity.EntityRule entityRule : entityRuleDao.selectAll()) {
                allEntityRules.put(entityRule.getId().toString(), entityRule);
            }
        }
    }

    private StudyRule loadStudy() throws MethodFailedException {
        loadAllEntityRules();
        List<com.acuity.visualisations.mapping.entity.StudyRule> studyRules = studyRuleDao.getByDrugNameAndStudyCode(projectName, studyCode);
        if (!studyRules.isEmpty()) {
            StudyRule studyRule = new StudyRule();
            studyRule.setName(studyRules.get(0).getStudyCode());
            studyRule.setId(studyRules.get(0).getId());
            studyRule.setCheckStudyCode(true);
            studyRule.setDisplayName(studyRules.get(0).getStudyName());
            studyMappingsServicePartial.getExistingFileRules(studyRules.get(0), true);
            studyRules.get(0).setExcludingValues(excludingValueService.getExcludingValuesByStudyRule(studyRules.get(0).getId()));
            studyRule.getFile().addAll(loadFileRules(studyRules.get(0)));
            return studyRule;
        } else {
            throw new MethodFailedException(String.format("Study with drug name \"%s\" and study code \"%s\" not found", projectName, studyCode));
        }
    }

    private List<DataFileRule> loadFileRules(com.acuity.visualisations.mapping.entity.StudyRule studyRule) {
        // load excluding values map
        Map<Long, List<String>> excludingValuesMap = new HashMap<>();
        for (ExcludingValue value : studyRule.getExcludingValues()) {
            List<String> values = excludingValuesMap.get(value.getFieldRuleId());
            if (values == null) {
                values = new ArrayList<>();
                excludingValuesMap.put(value.getFieldRuleId(), values);
            }
            values.add(value.getValue());
        }

        List<FileRule> fileRules = studyRule.getFileRules().stream().filter(FileRule::isAcuityEnabled).collect(Collectors.toList());
        Collections.sort(fileRules, (o1, o2) -> Double.compare(o1.getDescriptions().get(0).getProcessOrder(), o2.getDescriptions().get(0).getProcessOrder()));
        List<DataFileRule> result = new ArrayList<DataFileRule>();
        cntlinFileRules = new ArrayList<>(1);
        for (FileRule fileRule : fileRules) {
            if (fileRule.getName() != null) {
                DataFileRule item = null;
                for (int i = 0; i < result.size(); i++) { // hope it won't
                    // reduce
                    // performance,
                    if (result.get(i).getName().equalsIgnoreCase(fileRule.getName())) {
                        item = result.get(i);
                        break;
                    }
                }
                if (item == null || isCntlin(fileRule)) {
                    item = new DataFileRule();
                    item.setName(fileRule.getName());
                    item.setStandard(fileRule.getFileStandard());
                    item.setType(FileType.CUMULATIVE);
                }
                List<EntityRule> entities = loadEntityRules(fileRule, excludingValuesMap);
                item.getEntity().addAll(entities);
                if (isCntlin(fileRule)) {
                    cntlinFileRules.add(item);
                } else {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private boolean isCntlin(FileRule fileRule) {
        return fileRule.getDescriptions().get(0).getId().equals(CNTLIN_FILE_DESCRIPTION_ID);
    }

    private List<EntityRule> loadEntityRules(FileRule fileRule, Map<Long, List<String>> excludingValuesMap) {
        Map<String, List<MappingRule>> map = new LinkedHashMap<String, List<MappingRule>>();
        fileRule.getMappingRules().sort(Comparator.comparingInt(o -> o.getFieldRules().get(0).getEntityProcessOrder()));
        // group mappings by entities

        MappingRule refMapRule = fileRule.getMappingRules().get(0);
        FieldRule refField = refMapRule.getFieldRules().get(0);
        String refEntityRuleId = refField.getEntityRuleId();

        for (MappingRule mappingRule : fileRule.getMappingRules()) {

            for (FieldRule fieldRule : mappingRule.getFieldRules()) {

                if (fileRule.getDescriptions().get(0).getId().equals(fieldRule.getFileDescriptionId()) || fieldRule.getFileDescriptionId() == null) {
                    String entityId = fieldRule.getEntityRuleId();

                    if (entityId == null) {
                        entityId = refEntityRuleId;
                    }

                    List<MappingRule> mappingRules = map.get(entityId);
                    if (mappingRules == null) {
                        mappingRules = new ArrayList<>();
                        map.put(entityId, mappingRules);
                    }
                    mappingRules.add(mappingRule);
                }
            }
        }
        // fill entities
        List<EntityRule> result = new ArrayList<EntityRule>();
        for (Map.Entry<String, List<MappingRule>> entry : map.entrySet()) {
            com.acuity.visualisations.mapping.entity.EntityRule entityRule = allEntityRules.get(entry.getKey());
            if (entityRule != null) {
                EntityRule item = new EntityRule();
                item.setName(entityRule.getName());
                for (MappingRule mappingRule : entry.getValue()) {
                    for (FieldRule fieldRule : mappingRule.getFieldRules()) {
                        if (entry.getKey().equals(fieldRule.getEntityRuleId())
                                && fileRule.getDescriptions().get(0).getId().equals(fieldRule.getFileDescriptionId())
                                || fieldRule.getFileDescriptionId() == null) {
                            if (mappingRule.getColumnRules().size() == 0) {
                                ColumnRule columnRule = new ColumnRule();
                                columnRule.setName("");
                                columnRule.setSubjectField(SUBJECT_FIELD_DESCRIPTION_ID.equals(fieldRule.getDescription().getId()));
                                columnRule.setDescription(fieldRule.getDescription().getText());
                                columnRule.setField(fieldRule.getName());
                                columnRule.setType(fieldRule.getType());
                                columnRule.setFmtname(mappingRule.getFmtName());
                                columnRule.setDefault(mappingRule.getValue());
                                item.getColumn().add(columnRule);
                            } else {
                                for (com.acuity.visualisations.mapping.entity.ColumnRule rule : mappingRule.getColumnRules()) {
                                    ColumnRule columnRule = new ColumnRule();
                                    columnRule.setName(rule.getName());
                                    columnRule.setField(fieldRule.getName());
                                    columnRule.setSubjectField(SUBJECT_FIELD_DESCRIPTION_ID.equals(fieldRule.getDescription().getId()));
                                    columnRule.setDescription(fieldRule.getDescription().getText());
                                    columnRule.setType(fieldRule.getType());
                                    if (mappingRule.getColumnRules().indexOf(rule) == mappingRule.getColumnRules().size() - 1) {
                                        columnRule.setAggrFunction(mappingRule.getAggregationFunction().getName().equalsIgnoreCase(NO_AGGREGATION_TERM) ? null
                                                : mappingRule.getAggregationFunction().getName());
                                    }
                                    if (mappingRule.getAggregationFunction().getHelper() != null) {
                                        columnRule.setHelper(ParserRule.fromValue(mappingRule.getAggregationFunction().getHelper()));
                                    }
                                    columnRule.setFmtname(mappingRule.getFmtName());
                                    columnRule.setDefault(mappingRule.getValue());
                                    columnRule.setPart(mappingRule.getColumnRules().size() > 1
                                            || !NO_AGGREGATION_TERM.equalsIgnoreCase(mappingRule.getAggregationFunction().getName()));

                                    List<String> values = excludingValuesMap.get(fieldRule.getId());
                                    if (values != null) {
                                        columnRule.getExcludingValues().addAll(values);
                                    }
                                    item.getColumn().add(columnRule);
                                }
                            }
                        }
                    }
                }
                result.add(item);
            }
        }
        return result;
    }
}

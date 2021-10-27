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

import com.acuity.visualisations.mapping.dao.IAggregationFunctionDao;
import com.acuity.visualisations.mapping.dao.IColumnRuleDao;
import com.acuity.visualisations.mapping.dao.ICommonStaticDao;
import com.acuity.visualisations.mapping.dao.IDynamicFieldRuleDao;
import com.acuity.visualisations.mapping.dao.IFileDescriptionDao;
import com.acuity.visualisations.mapping.dao.IFileRuleDao;
import com.acuity.visualisations.mapping.dao.IFileSectionDao;
import com.acuity.visualisations.mapping.dao.IFileTypeDao;
import com.acuity.visualisations.mapping.dao.IMappingRuleDao;
import com.acuity.visualisations.mapping.dao.IRelationDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.AggregationFunction;
import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileSection;
import com.acuity.visualisations.mapping.entity.FileType;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudyRule.Status;
import com.acuity.visualisations.util.Pair;
import com.acuity.visualisations.web.service.IStudyMappingsServicePartial;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
@Transactional(readOnly = true)
@Service
public class StudyMappingsServicePartial implements IStudyMappingsServicePartial {

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Autowired
    private IFileRuleDao fileRuleDao;

    @Autowired
    private IFileTypeDao fileTypeDao;

    @Autowired
    private IFileSectionDao fileSectionDao;

    @Autowired
    private IMappingRuleDao mappingRuleDao;

    @Autowired
    private IColumnRuleDao columnRuleDao;

    @Autowired
    private IRelationDao relationDao;

    @Autowired
    private IFileDescriptionDao fileDescriptionDao;

    @Autowired
    private IAggregationFunctionDao aggregationFunctionDao;

    @Autowired
    private ICommonStaticDao commonStaticDao;

    @Autowired
    private IDynamicFieldRuleDao dynamicFieldRuleDao;

    private List<AggregationFunction> aggregationFunctions;
    private List<FileDescription> fileDescriptions;
    private List<FieldRule> fields;
    private List<FileType> fileTypes;


    @Override
    public FileRule getFileRule(long fileRuleId) {
        FileRule fileRule = fileRuleDao.getFileRule(fileRuleId);
        List<FileDescription> allFileDescriptions = getFileDescriptions();

        List<Long> fileDescriptionIds = relationDao.getFileDescriptionIdsForFileRule(fileRuleId);

        for (Long fileDescriptionId : fileDescriptionIds) {
            for (FileDescription fileDescription : allFileDescriptions) {
                if (fileDescription.getId().equals(fileDescriptionId)) {
                    fileRule.getDescriptions().add(fileDescription);
                    break;
                }
            }
        }

        List<AggregationFunction> allAggregationFunctions = getAggregationFunctions();
        List<FieldRule> allFieldRules = getFields();

        List<ColumnRule> columnRules = columnRuleDao.getColumnRulesForFileRule(fileRuleId);
        List<MappingRule> mappingRules = mappingRuleDao.getMappingRulesForFileRule(fileRuleId);
        List<Pair<Long, Long>> mappingRuleRelField = relationDao.getMappingRuleRelFieldForFileRule(fileRuleId);

        for (MappingRule mappingRule : mappingRules) {
            // join MappingRules with ColumnRules
            for (ColumnRule columnRule : columnRules) {
                if (columnRule.getMappingRuleId().equals(mappingRule.getId())) {
                    mappingRule.getColumnRules().add(columnRule);
                    columnRule.setMappingRule(mappingRule);
                }
            }
            // join 1:1 MappingRules with AggregationFunctions
            for (AggregationFunction aggregationFunction : allAggregationFunctions) {
                if (aggregationFunction.getId().equals(mappingRule.getAggregationFunctionId())) {
                    mappingRule.setAggregationFunction(aggregationFunction);
                    break;
                }
            }
            // join MappingRules with FileRules
            if (fileRule.getId().equals(mappingRule.getFileRuleId())) {
                fileRule.getMappingRules().add(mappingRule);
                mappingRule.setFileRule(fileRule);
            }

            // join MappingRules with Fields
            for (Pair<Long, Long> mrule : mappingRuleRelField) {
                if (mrule.getA().equals(mappingRule.getId())) {
                    for (FieldRule fieldRule : allFieldRules) {
                        if (fieldRule.getId().equals(mrule.getB())
                                && fieldRule.getFileDescriptionId().equals(mappingRule.getFileRule().getDescriptions().get(0).getId())) {
                            mappingRule.getFieldRules().add(fieldRule);
                            break;
                        }
                    }
                }
            }
        }

        return fileRule;
    }

    @Override
    public void getExistingFileRules(StudyRule study, boolean loadMappings) {
        List<FileRule> fileRules = fileRuleDao.getFileRulesByStudy(study.getId());

        List<Pair<Long, Long>> fileRuleRelFileDescr = relationDao.getFileRuleRelFileDescrByStudy(study.getId());
        List<FileDescription> allFileDescriptions = getFileDescriptions();

        // join FileRules with FileDescriptions
        for (FileRule fileRule : fileRules) {
            for (Pair<Long, Long> pair : fileRuleRelFileDescr) {
                if (fileRule.getId().equals(pair.getA())) {
                    for (FileDescription fileDescription : allFileDescriptions) {
                        if (fileDescription.getId().equals(pair.getB())) {
                            fileRule.getDescriptions().add(fileDescription);
                            break;
                        }
                    }
                }
            }
        }

        if (loadMappings) {
            List<AggregationFunction> allAggregationFunctions = getAggregationFunctions();
            List<FieldRule> allFieldRules = getFields();
            List<ColumnRule> columnRules = columnRuleDao.getColumnRulesByStudy(study.getId());
            List<MappingRule> mappingRules = mappingRuleDao.getMappingRulesByStudyRule(study.getId());
            List<Pair<Long, Long>> mappingRuleRelField = relationDao.getMappingRuleRelFieldByStudy(study.getId());

            for (MappingRule mappingRule : mappingRules) {
                // join MappingRules with ColumnRules
                for (ColumnRule columnRule : columnRules) {
                    if (columnRule.getMappingRuleId().equals(mappingRule.getId())) {
                        mappingRule.getColumnRules().add(columnRule);
                        columnRule.setMappingRule(mappingRule);
                    }
                }
                // join 1:1 MappingRules with AggregationFunctions
                for (AggregationFunction aggregationFunction : allAggregationFunctions) {
                    if (aggregationFunction.getId().equals(mappingRule.getAggregationFunctionId())) {
                        mappingRule.setAggregationFunction(aggregationFunction);
                        break;
                    }
                }
                // join MappingRules with FileRules
                for (FileRule fileRule : fileRules) {
                    if (fileRule.getId().equals(mappingRule.getFileRuleId())) {
                        fileRule.getMappingRules().add(mappingRule);
                        mappingRule.setFileRule(fileRule);
                    }
                }

                FieldRule dynamicField = getDynamicFieldRuleByMappingRule(mappingRule);

                if (dynamicField != null) {
                    mappingRule.getFieldRules().add(dynamicField);
                } else {
                    // join MappingRules with Fields
                    for (Pair<Long, Long> mrule : mappingRuleRelField) {
                        if (mrule.getA().equals(mappingRule.getId())) {
                            for (FieldRule fieldRule : allFieldRules) {
                                if (fieldRule.getId().equals(mrule.getB())
                                        && fieldRule.getFileDescriptionId().equals(mappingRule.getFileRule().getDescriptions().get(0).getId())) {
                                    mappingRule.getFieldRules().add(fieldRule);
                                    break;
                                }
                            }
                        }
                    }
                }

            }

            // create missing rules
            for (FileRule fileRule : fileRules) {
                createMissingRules(fileRule, allFieldRules);
            }
        }

        study.setFileRules(fileRules);
    }

    private void createMissingRules(FileRule fileRule, List<FieldRule> allFieldRules) {
        Map<Long, FieldRule> newFieldRules = new HashMap<>();
        for (FieldRule fieldRule : allFieldRules) {
            for (FileDescription fileDescription : fileRule.getDescriptions()) {
                if (fieldRule.getFileDescriptionId().equals(fileDescription.getId())) {
                    newFieldRules.put(fieldRule.getId(), fieldRule);
                    break;
                }
            }
        }
        for (MappingRule mappingRule : fileRule.getMappingRules()) {
            for (FieldRule fieldRule : mappingRule.getFieldRules()) {
                newFieldRules.remove(fieldRule.getId());
            }
        }
        for (FieldRule fieldRule : newFieldRules.values()) {
            MappingRule mappingRule = new MappingRule();
            mappingRule.setFileRule(fileRule);
            mappingRule.setAggregationFunction(getDefaultAggregationFunction());
            mappingRule.getFieldRules().add(fieldRule);
            fileRule.getMappingRules().add(mappingRule);
        }
        if (!newFieldRules.isEmpty()) {
            saveMappingRules(fileRule);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void checkValidStatus(StudyRule study) {
        boolean valid = study.isStudyValid();
        if (!study.isStudyCompleted() && valid) {
            valid = false;
        }

        if (study.isStudyValid() != valid) {
            study.setStudyValid(valid);
            studyRuleDao.update(study);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void deleteFileRule(FileRule fileRule, StudyRule study) {
        fileRule.setStudyRule(study);
        relationDao.deleteMapFileDescription(fileRule.getId());
        deleteMappingRules(fileRule);
        fileRuleDao.delete(fileRule);
        study.getFileRules().remove(fileRule);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void deleteMappingRules(@NonNull FileRule fileRule) {
        fileRule.getMappingRules().forEach(mappingRule -> {
            deleteMappingRule(fileRule.getStudyRule(), mappingRule);
            relationDao.deleteMappingFields(mappingRule);
            columnRuleDao.delete(mappingRule);
        });
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void deleteMappingRule(@NonNull MappingRule rule) {
        mappingRuleDao.delete(rule);
        Optional<StudyRule> study = ofNullable(rule.getFileRule()).map(FileRule::getStudyRule);
        deleteMappingRule(study.orElse(null), rule);
    }

    private void deleteMappingRule(StudyRule studyRule, @NonNull MappingRule rule) {
        studyRule = studyRule == null ? ofNullable(rule.getFileRule()).map(FileRule::getStudyRule).orElse(null) : studyRule;
        if (studyRule != null && !CollectionUtils.isEmpty(rule.getColumnRules())) {
            relationDao.deleteSubjectGroupingByStudyAndName(
                    studyRule.getId(),
                    rule.getColumnRules().stream().map(ColumnRule::getName));
        }
    }

    @Override
    public List<FileDescription> getFileDescriptions() {
        if (fileDescriptions == null) {
            fileDescriptions = fileDescriptionDao.selectAll();
        }
        return fileDescriptions;
    }

    @Override
    public List<AggregationFunction> getAggregationFunctions() {
        if (aggregationFunctions == null) {
            aggregationFunctions = aggregationFunctionDao.selectAll();
        }
        return aggregationFunctions;
    }

    @Override
    public List<FieldRule> getFields() {
        if (fields == null) {
            fields = commonStaticDao.getFieldsWithFileId();
        }
        return fields;
    }

    @Override
    public List<FileType> getFileTypes() {
        if (fileTypes == null) {
            fileTypes = fileTypeDao.selectAll();
        }
        return fileTypes;
    }

    @Override
    public AggregationFunction getDefaultAggregationFunction() {
        for (AggregationFunction f : getAggregationFunctions()) {
            if ("noAggregation".equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    @Override
    public List<FileSection> getFileSections() {
        List<FileDescription> files = getFileDescriptions();
        List<FileSection> sections = fileSectionDao.selectAll();

        for (FileDescription file : files) {
            for (FileSection section : sections) {
                if (section.getId().equals(file.getSectionId())) {
                    section.getFileDescriptions().add(file);
                }
            }
        }
        return sections;
    }

    @Override
    public void generateFileMappings(FileRule fileRule) {
        List<FieldRule> fields = new ArrayList<FieldRule>();
        for (FieldRule field : getFields()) {
            for (FileDescription desc : fileRule.getDescriptions()) {
                if (field.getFileDescriptionId().equals(desc.getId())) {
                    fields.add(field);
                    break;
                }
            }
        }

        for (FieldRule field : fields) {
            MappingRule mapping = new MappingRule();
            mapping.getFieldRules().add(field);
            mapping.setFileRule(fileRule);
            mapping.setAggregationFunction(getDefaultAggregationFunction());
            fileRule.getMappingRules().add(mapping);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveFileRule(FileRule fileRule) {
        if (fileRule.getId() == null) {
            fileRuleDao.insert(fileRule);
            relationDao.insertFileDescriptionRelations(fileRule);
        } else {
            fileRuleDao.update(fileRule);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void validateStudyCompleted(StudyRule study, List<FileSection> sections) {
        boolean completed = true;
        FileSection patientSection = null;
        for (FileSection section : sections) {
            if (section.getId().equals(FileSection.PATIENT_INFORMATION_SECTION)) {
                patientSection = section;
            }
        }
        Map<Long, Boolean> filesValid = new HashMap<Long, Boolean>();

        for (FileDescription fileDescription : patientSection.getFileDescriptions()) {
            filesValid.put(fileDescription.getId(), false);
        }

        for (FileRule fileRule : study.getFileRules()) {
            if (fileRule.getDescriptions().size() == 0) {
                continue;
            }
            boolean valid = fileRule.validate();
            boolean isPatientInfo = fileRule.getDescriptions().get(0).getSectionId().equals(patientSection.getId());
            if (valid && isPatientInfo) {
                filesValid.put(fileRule.getDescriptions().get(0).getId(), true);
            }
        }
        for (Boolean valid : filesValid.values()) {
            if (!valid) {
                completed = false;
                break;
            }
        }
        if (study.isStudyCompleted() != completed) {
            study.setStudyCompleted(completed);
            studyRuleDao.update(study);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public List<FileRule> validateMappings(StudyRule study) {
        List<FileRule> fileRules = new ArrayList<>();
        boolean mapped = study.getFileRules().size() != 0;
        for (FileRule fileRule : study.getFileRules()) {
            if (fileRule.getDescriptions().size() == 0) {
                continue;
            }
            boolean valid = fileRule.validate();
            if (!valid) {
                mapped = false;
            }
            fileRules.add(fileRule);
        }
        Status status = mapped ? Status.mapped : Status.incomplete;
        if (study.getFileRules().isEmpty()) {
            status = Status.readyToMap;
        }
        if (!study.getStatus().equals(status)) {
            study.setStatus(status);
            studyRuleDao.update(study);
        }
        return fileRules;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveMappingRules(FileRule fileRule) {
        for (MappingRule mappingRule : fileRule.getMappingRules()) {
            if (mappingRule.getId() == null) {
                mappingRuleDao.insert(mappingRule);
                relationDao.insertMappingFields(mappingRule);
                for (ColumnRule columnRule : mappingRule.getColumnRules()) {
                    columnRuleDao.insert(columnRule);
                }
            } else {
                mappingRuleDao.update(mappingRule);
                columnRuleDao.delete(mappingRule);
                for (ColumnRule columnRule : mappingRule.getColumnRules()) {
                    columnRuleDao.insert(columnRule);
                }
            }
        }
    }

    @Override
    public void validateStudyEnabled(StudyRule study) {
        boolean enabled = false;

        if (study.isStudyCompleted() && study.isStudyValid()) {

            final Map<Long, Boolean> fileTypeAnyEnabled = new HashMap<>();

            // check if present at least one isReactEnabled for each file type
            for (FileRule fileRule : study.getFileRules()) {
                if (!fileRule.getDescriptions().isEmpty()) {
                    FileDescription fd = fileRule.getDescriptions().get(0);
                    if (fd.getSectionId().equals(FileSection.PATIENT_INFORMATION_SECTION)) {
                        fileTypeAnyEnabled.put(fd.getId(), fileRule.isAcuityEnabled() || fileTypeAnyEnabled.getOrDefault(fd.getId(), false));
                    }
                }
            }

            enabled = !fileTypeAnyEnabled.containsValue(false);
        }
        if (study.isStudyEnabled() != enabled) {
            study.setStudyEnabled(enabled);
            studyRuleDao.update(study);
        }
    }

    @Override
    public FieldRule saveDynamicFieldRule(MappingRule mappingRule, String name) {
        return dynamicFieldRuleDao.insertDynamicField(mappingRule.getId(), name);
    }

    @Override
    public FieldRule getDynamicFieldRuleByMappingRule(MappingRule mappingRule) {
        return dynamicFieldRuleDao.getDynamicFieldByMappingRuleId(mappingRule.getId());
    }

    @Override
    public void deleteDynamicFieldRuleByMappingRule(MappingRule mappingRule) {
        dynamicFieldRuleDao.deleteDynamicField(mappingRule.getId());
    }
}

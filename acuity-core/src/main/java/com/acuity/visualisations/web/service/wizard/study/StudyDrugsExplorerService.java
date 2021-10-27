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

import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Micro-ETL, extracts doses data from the mapped files
 */
@Service
public class StudyDrugsExplorerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudyWizardService.class);
    @Autowired
    private SourceService sourceService;


    /**
     * Returns a set of drug names related to the study rule.
     * This set will contain:
     * - Drug names from dose mappings defaults
     * - Drug names from mapped dose files
     * - Drug names from the cntlin file (according to the decoding values from the mappings)
     *
     * @param studyRule
     * @return
     */
    public Set<String> getStudyDrugNames(StudyRule studyRule) {
        Set<String> drugNames = new HashSet<>();

        List<FileRule> dosedFileRules = filterDosedFileRules(studyRule.getFileRules());

        Set<String> cntlinDrugKeys = new HashSet<>();

        for (FileRule fileRule : dosedFileRules) {
            DrugMappingInfo drugMappingInfo = getDrugMappingInfo(fileRule.getMappingRules());

            if (!StringUtils.isEmpty(drugMappingInfo.drugDefaultName)) {
                // Drug name from dose mappings defaults
                drugNames.add(drugMappingInfo.drugDefaultName);
            }

            if (!StringUtils.isEmpty(drugMappingInfo.drugDecodingKey)) {
                // Drug name key for the cntlin
                cntlinDrugKeys.add(drugMappingInfo.drugDecodingKey);
            }

            if (!StringUtils.isEmpty(fileRule.getName())
                    && !StringUtils.isEmpty(drugMappingInfo.drugColumnName)
                    && StringUtils.isEmpty(drugMappingInfo.drugDecodingKey)) {
                try {
                    //Drug names from mapped dose files
                    drugNames.addAll(sourceService.getDistinctValuesFromFileForColumn(fileRule.getName(),
                            drugMappingInfo.drugColumnName));
                } catch (Exception e) {
                    LOGGER.warn("Can't read drug names from the file {} ", fileRule.getName());
                }
            }
        }

        if (!cntlinDrugKeys.isEmpty()) {
            List<FileRule> cntlinFileRules = filterCntlinFileRules(studyRule.getFileRules());

            if (!cntlinFileRules.isEmpty()) {
                for (FileRule fileRule : cntlinFileRules) {
                    if (!StringUtils.isEmpty(fileRule.getName())) {
                        try {
                            //Drug names from the cntlin file
                            drugNames.addAll(sourceService.getCntlinDistinctLabelsFilterByKeys(fileRule.getName(),
                                    cntlinDrugKeys));
                        } catch (Exception e) {
                            LOGGER.warn("Can't read drug names from the file {} ", fileRule.getName());
                        }
                    }
                }
            }
        }
        drugNames.remove(null);
        drugNames.remove("");
        return drugNames;
    }

    private static class DrugMappingInfo {
        private String drugDefaultName;
        private String drugDecodingKey;
        private String drugColumnName;
    }

    private static DrugMappingInfo getDrugMappingInfo(List<MappingRule> mappingRules) {
        DrugMappingInfo drugMappingInfo = new DrugMappingInfo();

        for (MappingRule mappingRule : mappingRules) {
            for (FieldRule fieldRule : mappingRule.getFieldRules()) {
                if ("Study Drug Name".equals(fieldRule.getDescription().getText())) {
                    drugMappingInfo = new DrugMappingInfo();
                    drugMappingInfo.drugDefaultName = mappingRule.getValue();
                    drugMappingInfo.drugDecodingKey = mappingRule.getFmtName();

                    if (!mappingRule.getColumnRules().isEmpty()) {
                        drugMappingInfo.drugColumnName = mappingRule.getColumnRules().get(0).getName();
                    }
                    break;
                }
            }
        }
        return drugMappingInfo;
    }

    private static List<FileRule> filterCntlinFileRules(List<FileRule> fileRules) {
        return fileRules.stream().filter(fileRule -> {
            for (FileDescription fileDescription : fileRule.getDescriptions()) {
                if ("Cntlin source".equals(fileDescription.getDescription())) {
                    return true;
                }
            }
            return false;
        }).collect(toList());
    }

    private static List<FileRule> filterDosedFileRules(List<FileRule> fileRules) {
        return fileRules.stream().filter(fileRule -> {
            for (FileDescription fileDescription : fileRule.getDescriptions()) {
                if ("MedDosingSchedule source".equals(fileDescription.getDescription())) {
                    return true;
                }
            }
            return false;
        }).collect(toList());
    }
}

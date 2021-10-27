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

package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileStandard;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileRuleDTO {

    private Long id;
    private Long typeId;
    private String dataSourceLocation;
    private String dataSourceLocationType;
    private boolean studyAcuityEnabled = false;
    private boolean studyUpdateAcuityEnabled = false;

    private boolean mappingsPrediction = false;
    private FileStandard fileStandard;

    private transient boolean skipFileRuleCreationIfFilePredictionFailed = false;

    public FileRuleDTO() {
    }

    public FileRuleDTO(FileRule rule) {
        id = rule.getId();
        if (!rule.getDescriptions().isEmpty()) {
            typeId = rule.getDescriptions().get(0).getId();
        }
        dataSourceLocation = rule.getName();
        studyAcuityEnabled = rule.isAcuityEnabled();
        studyUpdateAcuityEnabled = rule.isUpdateEnabled();
        fileStandard = rule.getFileStandard();
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeInfo) {
        this.typeId = typeInfo;
    }

    public String getDataSourceLocation() {
        return dataSourceLocation;
    }

    public void setDataSourceLocation(String dataSourceLocation) {
        this.dataSourceLocation = dataSourceLocation;
    }

    public boolean getStudyAcuityEnabled() {
        return studyAcuityEnabled;
    }

    public void setStudyAcuityEnabled(boolean studyAcuityEnabled) {
        this.studyAcuityEnabled = studyAcuityEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(String id) {
        try {
            this.id = Long.parseLong(id);
        } catch (Exception e) {
            this.id = null;
        }
    }

    public FileStandard getFileStandard() {
        return fileStandard;
    }

    public void setFileStandard(FileStandard fileStandard) {
        this.fileStandard = fileStandard;
    }

    public String getDataSourceLocationType() {
        return dataSourceLocationType;
    }

    public void setDataSourceLocationType(String dataSourceLocationType) {
        this.dataSourceLocationType = dataSourceLocationType;
    }

    public boolean getStudyUpdateAcuityEnabled() {
        return studyUpdateAcuityEnabled;
    }

    public void setStudyUpdateAcuityEnabled(Boolean studyUpdateAcuityEnabled) {
        this.studyUpdateAcuityEnabled = studyUpdateAcuityEnabled;
    }

    public boolean isMappingsPrediction() {
        return mappingsPrediction;
    }

    public void setMappingsPrediction(boolean mappingsPrediction) {
        this.mappingsPrediction = mappingsPrediction;
    }

    public boolean isSkipFileRuleCreationIfFilePredictionFailed() {
        return skipFileRuleCreationIfFilePredictionFailed;
    }

    public void setSkipFileRuleCreationIfFilePredictionFailed(boolean skipFileRuleCreationIfFilePredictionFailed) {
        this.skipFileRuleCreationIfFilePredictionFailed = skipFileRuleCreationIfFilePredictionFailed;
    }
}

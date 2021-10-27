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

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class MapRulesDTO {

    private Long fileRuleId;

    private boolean studyAcuityEnabled = false;

    private List<MapRuleDTO> mapRules;

    public Long getFileRuleId() {
        return fileRuleId;
    }

    public void setFileRuleId(Long fileRuleId) {
        this.fileRuleId = fileRuleId;
    }

    public List<MapRuleDTO> getMapRules() {
        return mapRules;
    }

    public void setMapRules(List<MapRuleDTO> mapRules) {
        this.mapRules = mapRules;
    }

    public MapRuleDTO findMapRuleById(Long id) {
        for (MapRuleDTO rule : mapRules) {
            if (rule.getId().equals(id)) {
                return rule;
            }
        }
        return null;
    }

    public MapRuleDTO findMapRuleByDataField(String desc) {
        for (MapRuleDTO rule : mapRules) {
            if (rule.getDataField().equals(desc)) {
                return rule;
            }
        }
        return null;
    }

    public boolean isStudyAcuityEnabled() {
        return studyAcuityEnabled;
    }

    public void setStudyAcuityEnabled(boolean studyAcuityEnabled) {
        this.studyAcuityEnabled = studyAcuityEnabled;
    }
}

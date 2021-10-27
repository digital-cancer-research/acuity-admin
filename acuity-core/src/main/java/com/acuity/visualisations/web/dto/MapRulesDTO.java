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

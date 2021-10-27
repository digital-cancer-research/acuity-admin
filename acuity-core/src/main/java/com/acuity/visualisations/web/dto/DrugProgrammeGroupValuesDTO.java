package com.acuity.visualisations.web.dto;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class DrugProgrammeGroupValuesDTO {

    private List<ProjectGroupValueDTO> values;
    private Long id;
    private String groupType;

    public List<ProjectGroupValueDTO> getValues() {
        return values;
    }

    public void setValues(List<ProjectGroupValueDTO> values) {
        this.values = values;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}

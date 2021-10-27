package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.SubjectGroupValueRule;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class SubjectGroupValuesDTO {

    private Long groupId;
    private List<SubjectGroupValueRule> values;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<SubjectGroupValueRule> getValues() {
        return values;
    }

    public void setValues(List<SubjectGroupValueRule> values) {
        this.values = values;
    }
}

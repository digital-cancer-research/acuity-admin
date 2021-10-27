package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGroup {
    private Long id;
    private Integer index;
    private String groupingName;
    private String groupName;
    private String groupPreferedName;
    private Integer subjectsCount;

    private List<SubjectGroupDosing> dosings;

    public SubjectGroup(String groupName, String groupPreferedName) {
        this.groupName = groupName;
        this.groupPreferedName = groupPreferedName;
    }

    public SubjectGroup(String groupingName, String groupName, String groupPreferedName) {
        this.groupingName = groupingName;
        this.groupName = groupName;
        this.groupPreferedName = groupPreferedName;
    }
}

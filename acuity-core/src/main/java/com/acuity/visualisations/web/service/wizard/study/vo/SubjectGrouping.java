package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("SubjectGrouping")
public class SubjectGrouping {
    private Long id;
    private String name;
    private SubjectGroupingType type = SubjectGroupingType.NONE;
    @Getter
    @Setter
    private List<SubjectGroup> groups = new ArrayList<>();

    public SubjectGrouping(String name) {
        this.name = name;
    }

    public SubjectGrouping(Long id, String name, SubjectGroupingType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.groups = new ArrayList<>();
    }

}


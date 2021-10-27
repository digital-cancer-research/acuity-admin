package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("SubjectGroupingType")
public class SubjectGroupingType {

    public static final SubjectGroupingType NONE = new SubjectGroupingType(0L, "NONE");

    private Long id;
    private String name;
}

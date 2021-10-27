package com.acuity.visualisations.web.service.wizard.study.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectGroupings {
    private String studyName;
    private String studyCode;

    private List<SubjectGrouping> groupings;
}

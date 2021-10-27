package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGroupDosingSchedule {
    private Long id;

    private boolean dosing;
    private boolean repeat;

    private Integer duration;
    private SubjectGroupTimeUnit durationUnit;

    private Double dose;
    private String doseUnit;

    private Integer frequency;
    private String frequencyUnit;
    private String frequencyTerm;
}

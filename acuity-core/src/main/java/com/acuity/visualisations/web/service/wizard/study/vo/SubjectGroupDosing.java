package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGroupDosing {
    private Long id;
    private String drug;
    private String formulation;
    private String administrationRoute;
    private SubjectGroupTimeUnit totalDurationType;
    private Integer totalDurationCycles;

    private List<SubjectGroupDosingSchedule> schedule;

    private transient String doseSchedule;
    private transient String dosingContinuity;

    public SubjectGroupDosing(String drug, String formulation, String administrationRoute, SubjectGroupTimeUnit totalDurationType,
                              Integer totalDurationCycles) {
        this.drug = drug;
        this.formulation = formulation;
        this.administrationRoute = administrationRoute;
        this.totalDurationType = totalDurationType;
        this.totalDurationCycles = totalDurationCycles;
    }
}

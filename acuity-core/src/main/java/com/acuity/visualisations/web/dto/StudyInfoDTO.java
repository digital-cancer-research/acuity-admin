package com.acuity.visualisations.web.dto;

import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.util.JSONDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyInfoDTO {
    @Getter
    @Setter
    private String studyCode;
    @Getter
    @Setter
    private String studyName;
    @Getter
    @Setter
    private String clinicalStudyName;
    @Getter
    @Setter
    private String clinicalStudyId;
    @Getter
    @Setter
    private String phase;

    @Getter
    @Setter
    private Boolean canEditStudy;
    @Getter
    @Setter
    private Boolean canDeleteStudy;

    @Getter
    @Setter
    @JsonSerialize(using = JSONDateSerializer.class)
    private Date firstSubjectInPlanned;
    @Getter
    @Setter
    @JsonSerialize(using = JSONDateSerializer.class)
    private Date databaseLockPlanned;

    @Getter
    @Setter
    private boolean blinding;
    @Getter
    @Setter
    private boolean randomisation;
    @Getter
    @Setter
    private boolean regulatory;
    @Getter
    @Setter
    private List<FileRuleDTO> fileRules;

    public StudyInfoDTO(StudyRule rule) {
        studyCode = rule.getStudyCode();
        studyName = rule.getStudyName();
        clinicalStudyId = rule.getClinicalStudyId();
        clinicalStudyName = rule.getClinicalStudyName();
        phase = rule.getPhase();
        firstSubjectInPlanned = rule.getFirstSubjectInPlanned();
        databaseLockPlanned = rule.getDatabaseLockPlanned();

        blinding = rule.isBlinding();
        randomisation = rule.isRandomisation();
        regulatory = rule.isRegulatory();
    }

    public void setFileRules(List<FileRule> fileRules) {
        this.fileRules = new ArrayList<>(fileRules.size());
        for (FileRule fileRule : fileRules) {
            this.fileRules.add(new FileRuleDTO(fileRule));
        }
    }
}

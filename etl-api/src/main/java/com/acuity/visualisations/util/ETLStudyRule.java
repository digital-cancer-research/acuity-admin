package com.acuity.visualisations.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ETLStudyRule {
    private String studyCode;
    private String drugName;
    private String cronExpression;
    private boolean scheduled;
    private boolean scheduledClean;
    private boolean amlEnabled;
}

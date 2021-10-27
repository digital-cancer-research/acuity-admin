package com.acuity.visualisations.model.aml;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class Ecg {

    private String id;
    private String studyId;
    private String subject;
    private Date date;
    private Double qtInterval;
    private Double visitNumber;

}

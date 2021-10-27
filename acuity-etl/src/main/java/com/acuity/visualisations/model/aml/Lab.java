package com.acuity.visualisations.model.aml;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class Lab {

    private String id;
    private String studyId;
    private String subject;
    private String labCode;
    private String unit;
    private Date testDate;
    private Double value;
    private Double visitNumber;

}

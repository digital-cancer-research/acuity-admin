package com.acuity.visualisations.model.aml;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class Conmed {

    private String id;
    private String studyId;
    private String subject;
    private Date startDate;
    private Date endDate;

}

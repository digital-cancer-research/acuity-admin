package com.acuity.visualisations.model.aml;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class AlgorithmOutcome {

    private String id;
    private String eventId;
    private EventType eventType;
    private String sourceId;
    private String result;
    private Date createdDate;
    private Date updatedDate;

}

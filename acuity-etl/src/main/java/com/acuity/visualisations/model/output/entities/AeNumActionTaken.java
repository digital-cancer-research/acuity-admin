package com.acuity.visualisations.model.output.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AeNumActionTaken extends TimestampedEntity {
    private String patientGuid;
    private String doseGuid;

    private Object numActionTaken;

    //We need these fields only for foreign keys to Dose and Patient tables
    private transient String drug;
    private transient LocalDateTime startDate;
    private transient String subject;
    private transient String studyName;
    private transient String projectName;
}

package com.acuity.visualisations.web.entity;

public enum ReportErrorType {

    UNKNOWN(-1),
    FK_CONSTRAINT(0),
    MISSED_CNTLIN(1),
    UNIQUE_VIOLATION(2),
    UNPARSED_DATA(3);

    private int value;
    
    ReportErrorType(int value) {
        this.value = value;
    }
}

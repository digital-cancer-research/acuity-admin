package com.acuity.visualisations.web.exception;

public class ClinicalStudyException extends Exception {

    private static final long serialVersionUID = 6680469388274189590L;

    public ClinicalStudyException(String s) {
        super(s);
    }

    public ClinicalStudyException(String s, Exception e) {
        super(s, e);
    }

}

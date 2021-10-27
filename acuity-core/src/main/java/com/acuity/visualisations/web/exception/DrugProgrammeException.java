package com.acuity.visualisations.web.exception;

public class DrugProgrammeException extends Exception {

    private static final long serialVersionUID = 2188287048925880644L;

    public DrugProgrammeException(String s) {
        super(s);
    }

    public DrugProgrammeException(String s, Exception e) {
        super(s, e);
    }

}

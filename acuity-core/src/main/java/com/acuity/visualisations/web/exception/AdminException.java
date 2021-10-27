package com.acuity.visualisations.web.exception;

public class AdminException extends Exception {

    private static final long serialVersionUID = 2143005223126229645L;

    public AdminException(String s) {
        super(s);
    }

    public AdminException(String s, Exception e) {
        super(s, e);
    }

}

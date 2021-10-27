package com.acuity.visualisations.exception;

public class MethodFailedException extends Exception {
    public MethodFailedException(String format) {
        super(format);
    }

    public MethodFailedException(String message, Exception e) {
        super(message, e);
    }
}

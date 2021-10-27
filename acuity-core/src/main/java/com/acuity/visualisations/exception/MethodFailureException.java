package com.acuity.visualisations.exception;

public class MethodFailureException extends RuntimeException {

    private static final long serialVersionUID = 5345813629122612500L;

    public MethodFailureException() {

    }

    public MethodFailureException(String message) {
        super(message);
    }

    public MethodFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodFailureException(Throwable cause) {
        super(cause);
    }

}

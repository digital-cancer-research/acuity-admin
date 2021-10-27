package com.acuity.visualisations.exception;

public class InvalidDataFormatException extends Exception {

    private static final long serialVersionUID = 7096163651225169611L;

    public InvalidDataFormatException() {
        super();
    }

    public InvalidDataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataFormatException(String message) {
        super(message);
    }

    public InvalidDataFormatException(Throwable cause) {
        super(cause);
    }

}

package com.acuity.visualisations.exception;

public class DataConversionException extends RuntimeException {

    private static final long serialVersionUID = -6234016664460272369L;

    public DataConversionException() {
    }

    public DataConversionException(String message) {
        super(message);
    }

    public DataConversionException(Throwable cause) {
        super(cause);
    }

    public DataConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}

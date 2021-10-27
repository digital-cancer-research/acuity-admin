package com.acuity.visualisations.exception;

public class EntityDaoException extends RuntimeException {

    private static final long serialVersionUID = -854438100309855970L;

    public EntityDaoException() {
    }

    public EntityDaoException(String message) {
        super(message);
    }

    public EntityDaoException(Throwable cause) {
        super(cause);
    }

    public EntityDaoException(String message, Throwable cause) {
        super(message, cause);
    }

}

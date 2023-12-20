package com.grupo7.cuentasclaras2.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private final String errorKey;

    public UserAlreadyExistsException(String message, String errorKey) {
        super(message);
        this.errorKey = errorKey;
    }

    public UserAlreadyExistsException(String message, Throwable cause, String errorKey) {
        super(message, cause);
        this.errorKey = errorKey;
    }

    public UserAlreadyExistsException(Throwable cause, String errorKey) {
        super(cause);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }

}

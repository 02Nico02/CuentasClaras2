package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar errores relacionados con usuarios en
 * la aplicación.
 */
public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

}

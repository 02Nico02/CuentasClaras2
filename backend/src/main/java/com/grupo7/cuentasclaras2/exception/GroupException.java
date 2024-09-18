package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar errores relacionados con grupos.
 */
public class GroupException extends RuntimeException {

    public GroupException(String message) {
        super(message);
    }

    public GroupException(String message, Throwable cause) {
        super(message, cause);
    }
}

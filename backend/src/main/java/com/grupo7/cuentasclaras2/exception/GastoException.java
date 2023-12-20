package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar errores relacionados con gastos en
 * la aplicación.
 */
public class GastoException extends RuntimeException {

    public GastoException(String message) {
        super(message);
    }

    public GastoException(String message, Throwable cause) {
        super(message, cause);
    }

    public GastoException(Throwable cause) {
        super(cause);
    }
}

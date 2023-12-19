package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar intentos no autorizados o fallidos
 * de acceder a recursos o realizar acciones.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}

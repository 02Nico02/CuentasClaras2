package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar problemas en el proceso de pagos.
 */
public class PagoException extends RuntimeException {

    public PagoException(String message) {
        super(message);
    }

    public PagoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PagoException(Throwable cause) {
        super(cause);
    }

}

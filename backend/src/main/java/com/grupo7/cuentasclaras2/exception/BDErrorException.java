package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar errores relacionados con la base de
 * datos en la aplicación.
 */
public class BDErrorException extends RuntimeException {

    public BDErrorException(String message) {
        super(message);
    }

    public BDErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BDErrorException(Throwable cause) {
        super(cause);
    }

}

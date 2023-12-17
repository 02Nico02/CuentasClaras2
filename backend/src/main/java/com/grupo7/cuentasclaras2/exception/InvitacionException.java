package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar problemas en el manejo de
 * invitaciones.
 */
public class InvitacionException extends RuntimeException {

    public InvitacionException(String message) {
        super(message);
    }

    public InvitacionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvitacionException(Throwable cause) {
        super(cause);
    }
}

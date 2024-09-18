package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar problemas en el manejo de
 * invitaciones a grupos.
 */
public class InvitationGroupException extends RuntimeException {

    public InvitationGroupException(String message) {
        super(message);
    }

    public InvitationGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvitationGroupException(Throwable cause) {
        super(cause);
    }

}

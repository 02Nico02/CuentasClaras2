package com.grupo7.cuentasclaras2.exception;

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

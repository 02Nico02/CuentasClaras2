package com.grupo7.cuentasclaras2.exception;

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

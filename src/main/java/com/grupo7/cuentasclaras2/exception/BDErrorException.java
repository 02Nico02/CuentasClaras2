package com.grupo7.cuentasclaras2.exception;

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

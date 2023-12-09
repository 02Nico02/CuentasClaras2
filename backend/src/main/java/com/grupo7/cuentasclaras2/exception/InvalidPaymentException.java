package com.grupo7.cuentasclaras2.exception;

public class InvalidPaymentException extends RuntimeException {

    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPaymentException(Throwable cause) {
        super(cause);
    }
}

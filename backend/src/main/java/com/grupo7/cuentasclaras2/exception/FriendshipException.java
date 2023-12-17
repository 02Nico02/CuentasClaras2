package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar errores relacionados con la gestión
 * de amistades en la aplicación.
 */
public class FriendshipException extends RuntimeException {

    public FriendshipException(String message) {
        super(message);
    }

    public FriendshipException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendshipException(Throwable cause) {
        super(cause);
    }

}

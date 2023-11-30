package com.grupo7.cuentasclaras2.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String field) {
        super(getMessage(field));
    }

    private static String getMessage(String field) {
        if ("username".equals(field)) {
            return "El nombre de usuario ya está en uso";
        } else if ("email".equals(field)) {
            return "El correo electrónico ya está en uso";
        } else {
            return "Error: campo duplicado";
        }
    }
}

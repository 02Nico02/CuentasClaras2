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

    /**
     * Crea una nueva instancia de PagoException con un mensaje y detalles
     * adicionales.
     *
     * @param message    El mensaje de error.
     * @param resourceId El identificador del recurso relacionado con el error (por
     *                   ejemplo, el ID del usuario o grupo).
     */
    public PagoException(String message, Object resourceId) {
        super(message + " [Recurso ID: " + resourceId + "]");
    }

    /**
     * Crea una nueva instancia de PagoException con un mensaje y detalles
     * adicionales.
     *
     * @param message    El mensaje de error.
     * @param resourceId El identificador del recurso relacionado con el error (por
     *                   ejemplo, el ID del usuario o grupo).
     * @param cause      La causa original del error.
     */
    public PagoException(String message, Object resourceId, Throwable cause) {
        super(message + " [Recurso ID: " + resourceId + "]", cause);
    }

}

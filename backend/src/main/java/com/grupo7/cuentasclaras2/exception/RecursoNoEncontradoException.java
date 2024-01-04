package com.grupo7.cuentasclaras2.exception;

/**
 * Excepción personalizada para representar un recurso no encontrado.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    /**
     * Constructor con un mensaje específico.
     *
     * @param recurso El nombre o descripción del recurso que no se encontró.
     * @param id      El identificador del recurso que no se encontró.
     */
    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No se encontró el recurso '" + recurso + "' con ID: " + id);
    }

    /**
     * Constructor con un mensaje específico y una causa.
     *
     * @param recurso El nombre o descripción del recurso que no se encontró.
     * @param id      El identificador del recurso que no se encontró.
     * @param cause   La causa de la excepción.
     */
    public RecursoNoEncontradoException(String recurso, Object id, Throwable cause) {
        super("No se encontró el recurso '" + recurso + "' con ID: " + id, cause);
    }
}

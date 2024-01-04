package com.grupo7.cuentasclaras2.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Clase que maneja las excepciones globalmente en la aplicación.
 * Proporciona respuestas HTTP adecuadas para diferentes tipos de excepciones.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Manejador para RecursoNoEncontradoException.
     *
     * @param ex La excepción RecursoNoEncontradoException.
     * @return Una respuesta ResponseEntity con el mensaje de error y el código de
     *         estado HTTP.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> handleRecursoNoEncontradoException(RecursoNoEncontradoException ex) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", ex.getMessage());

        try {
            String errorResponseJson = objectMapper.writeValueAsString(responseBody);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("field", ex.getErrorKey());
        return createJsonResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvitacionException.class)
    public ResponseEntity<String> handleInvitacionException(InvitacionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(FriendshipException.class)
    public ResponseEntity<String> handleFriendshipException(FriendshipException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PagoException.class)
    public ResponseEntity<String> handlePagoException(PagoException ex) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", ex.getMessage());

        try {
            String jsonResponse = objectMapper.writeValueAsString(responseBody);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }

    @ExceptionHandler(GastoException.class)
    public ResponseEntity<String> handleGastoException(GastoException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BDErrorException.class)
    public ResponseEntity<String> handleBDErrorException(BDErrorException ex) {
        log.error("Error en la base", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<String> handleInvalidPaymentException(InvalidPaymentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(GroupException.class)
    public ResponseEntity<String> handleGroupException(GroupException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvitationGroupException.class)
    public ResponseEntity<String> handleInvitationGroupException(InvitationGroupException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso no encontrado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Error no manejado", ex);

        return ResponseEntity.internalServerError().body("Error interno del servidor");
    }

    private ResponseEntity<String> createErrorResponse(String errorMessage, HttpStatus httpStatus) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", errorMessage);

        try {
            String jsonResponse = objectMapper.writeValueAsString(responseBody);
            return ResponseEntity.status(httpStatus).body(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }

    private ResponseEntity<Map<String, String>> createJsonResponse(Map<String, String> responseBody,
            HttpStatus httpStatus) {
        try {
            return ResponseEntity.status(httpStatus).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al procesar la solicitud"));
        }
    }
}

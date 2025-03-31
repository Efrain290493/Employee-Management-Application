package com.amaris.employee_management.exception;

import com.amaris.employee_management.exception.FeignErrorDecoder.ApiException;
import com.amaris.employee_management.exception.FeignErrorDecoder.RateLimitExceededException;
import com.amaris.employee_management.exception.FeignErrorDecoder.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador global para manejar excepciones de manera uniforme.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de recursos no encontrados.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> body = createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getReason(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de límite de tasa excedido.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimitExceededException(
            RateLimitExceededException ex, WebRequest request) {

        Map<String, Object> body = createErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate Limit Excedido",
                "Se ha alcanzado el límite de solicitudes a la API externa. " +
                        "Usando datos en caché si están disponibles.",
                request.getDescription(false)
        );

        log.warn("Rate limit excedido en la solicitud: {}", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.OK); // Devolvemos 200 OK para indicar que usaremos caché
    }

    /**
     * Maneja excepciones de la API.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(
            ApiException ex, WebRequest request) {

        Map<String, Object> body = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error de API Externa",
                ex.getReason(),
                request.getDescription(false)
        );

        log.error("Error de API externa: {}", ex.getReason());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja excepciones generales de estado de respuesta.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {

        Map<String, Object> body = createErrorResponse(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    /**
     * Maneja cualquier otra excepción no capturada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        log.error("Error no controlado", ex);

        Map<String, Object> body = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ha ocurrido un error inesperado. Por favor contacte al administrador.",
                request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Crea una estructura de respuesta de error estandarizada.
     */
    private Map<String, Object> createErrorResponse(
            int status, String error, String message, String path) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);

        return body;
    }
}
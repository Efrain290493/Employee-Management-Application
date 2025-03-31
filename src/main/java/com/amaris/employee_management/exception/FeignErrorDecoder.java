package com.amaris.employee_management.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        String responseBody = getResponseBody(response);

        log.debug("Error en llamada Feign - URL: {}, Status: {}, Body: {}",
                requestUrl, responseStatus, responseBody);

        switch (responseStatus) {
            case NOT_FOUND:
                return new ResourceNotFoundException("Recurso no encontrado: " + requestUrl);
            case BAD_REQUEST:
                return new BadRequestException("Solicitud incorrecta: " + responseBody);
            case UNAUTHORIZED:
                return new UnauthorizedException("No autorizado para acceder a: " + requestUrl);
            case FORBIDDEN:
                return new ForbiddenException("Acceso prohibido a: " + requestUrl);
            case TOO_MANY_REQUESTS:
                log.warn("Se ha alcanzado el límite de tasa de la API externa. URL: {}", requestUrl);
                return new RateLimitExceededException("Se ha alcanzado el límite de tasa de la API externa. Reintente más tarde.");
            default:
                return new ApiException("Error en la API externa: " + responseBody + " (Status: " + responseStatus + ")");
        }
    }

    private String getResponseBody(Response response) {
        if (response.body() == null) {
            return "No response body";
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return "Error reading response body: " + e.getMessage();
        }
    }

    public static class ResourceNotFoundException extends ResponseStatusException {
        public ResourceNotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message);
        }
    }

    public static class BadRequestException extends ResponseStatusException {
        public BadRequestException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }
    }

    public static class UnauthorizedException extends ResponseStatusException {
        public UnauthorizedException(String message) {
            super(HttpStatus.UNAUTHORIZED, message);
        }
    }

    public static class ForbiddenException extends ResponseStatusException {
        public ForbiddenException(String message) {
            super(HttpStatus.FORBIDDEN, message);
        }
    }

    public static class RateLimitExceededException extends ResponseStatusException {
        public RateLimitExceededException(String message) {
            super(HttpStatus.TOO_MANY_REQUESTS, message);
        }
    }

    public static class ApiException extends ResponseStatusException {
        public ApiException(String message) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }
}
package com.webelec.client.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSort(PropertyReferenceException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "code", "INVALID_SORT_FIELD",
                "field", ex.getPropertyName(),
                "message", "Champ de tri invalide: " + ex.getPropertyName()
        );
        return ResponseEntity.badRequest().body(body);
    }
}
package com.expensemanager.exception;

import com.expensemanager.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Thrown deliberately by services (e.g. "username taken", "invalid credentials")
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(
                ApiErrorResponse.of(status.value(), status.getReasonPhrase(),
                        List.of(ex.getReason() != null ? ex.getReason() : "Request could not be processed")));
    }

    // @Valid failures on request DTOs (e.g. missing/short password)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(400, "Validation failed", details));
    }

    // Catch-all: log the error and never leak internal stack traces to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Unhandled exception occurred: ", ex);
        return ResponseEntity.internalServerError().body(
                ApiErrorResponse.of(500, "Internal Server Error",
                        List.of("Something went wrong: " + ex.getMessage())));
    }
}

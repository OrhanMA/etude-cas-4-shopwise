package com.shopwise.app.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class CustomExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String message = error.getDefaultMessage();
              errors.put(fieldName, message);
            });
    return ResponseEntity.badRequest()
        .body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
  }

  @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleParameterType(Exception ex) {
    return ResponseEntity.badRequest().body(new ApiError(400, "Invalid parameter type"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleAll(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiError(500, "An unexpected error occurred"));
  }

  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleMalformedBody(Exception ex) {
    return ResponseEntity.badRequest().body(new ApiError(400, "Invalid JSON body"));
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleConflict(Exception ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError(409, "Resource conflicts with existing data"));
  }

  @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleMethod(Exception ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(new ApiError(405, "Method not allowed"));
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<ApiError> handleMissingRoute(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(404, "Resource not found"));
  }

}

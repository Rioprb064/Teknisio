package com.teknisio.common.exception;

import com.teknisio.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException exception) {
    String message = exception.getReason() != null
      ? exception.getReason()
      : "Invalid request";

    return ResponseEntity
      .status(exception.getStatusCode())
      .body(ApiResponse.error(message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationError(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new LinkedHashMap<>();

    for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
      errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }

    exception.getBindingResult().getGlobalErrors()
      .forEach(error -> errors.putIfAbsent(
        error.getObjectName(),
        error.getDefaultMessage()
      ));

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.error("Validation failed", errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
    Map<String, String> errors = exception.getConstraintViolations()
      .stream()
      .collect(Collectors.toMap(
        this::getPropertyName,
        ConstraintViolation::getMessage,
        (first, second) -> first,
        LinkedHashMap::new
      ));

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.error("Validation failed", errors));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ApiResponse.error(exception.getMessage()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException exception) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.error(exception.getMessage()));
  }

  @ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(Exception exception) {
    String message = exception.getMessage() != null
      ? exception.getMessage()
      : "Unauthorized";

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(ApiResponse.error(message));
  }

  @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
  public ResponseEntity<ApiResponse<Void>> handleForbidden(Exception exception) {
    String message = exception.getMessage() != null
      ? exception.getMessage()
      : "Forbidden";

    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(ApiResponse.error(message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleInternalServerError(Exception exception) {
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.error("Internal server error"));
  }

  private String getPropertyName(ConstraintViolation<?> violation) {
    String propertyPath = violation.getPropertyPath().toString();
    int lastDotIndex = propertyPath.lastIndexOf('.');

    if (lastDotIndex >= 0 && lastDotIndex < propertyPath.length() - 1) {
      return propertyPath.substring(lastDotIndex + 1);
    }

    return propertyPath;
  }
}

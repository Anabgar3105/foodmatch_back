package edu.abga.foodmatch.exception;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * The exception handler for the application
 * <p>Responsible for catching and handling exceptions thrown by the service layer and any unexpected errors that occur during request processing.
 * It provides a centralized mechanism to handle exceptions and return consistent error responses to the client with standardized error codes.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches custom exceptions (FoodMatchException).
     * @param ex the custom exception thrown by the service layer.
     * @param request the HTTP request that caused the exception, used to extract the request URI for error details.
     * @return a ResponseEntity containing a structured ErrorResponse with details about the error, and the appropriate HTTP status code.
     */
    @ExceptionHandler(FoodMatchException.class)
    public ResponseEntity<ErrorResponse> handleFoodMatchException(FoodMatchException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }

    /**
     * Catches expired JWT token exceptions.
     * @param ex the ExpiredJwtException thrown when the JWT token has expired.
     * @param request the HTTP request that caused the exception, used to extract the request URI for error details.
     * @return a ResponseEntity containing a structured ErrorResponse with a 401 Unauthorized status and message about the expired session.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .code(ErrorCode.TOKEN_EXPIRED.getCode())
                .message(ErrorCode.TOKEN_EXPIRED.getUserMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Catches any other unexpected error (Error 500).
     * @param ex the generic exception that was not handled by specific handlers, indicating an unexpected error in the application.
     * @param request the HTTP request that caused the exception, used to extract the request URI for error details.
     * @return a ResponseEntity containing a structured ErrorResponse with details about the error, and HTTP status code 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getUserMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.internalServerError().body(error);
    }
}
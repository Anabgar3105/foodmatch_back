package edu.abga.foodmatch.exception;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standardized structure for error responses sent to the client when an exception occurs in the application.
 * <p>This class encapsulates details about the error, including the timestamp, HTTP status code, error code,
 * error message, and the request path that caused the error.
 * It is used by the {@link GlobalExceptionHandler} to create consistent error responses.</p>
 */
@Data
@Builder
public class ErrorResponse {
    /**
     * The timestamp
     */
    private LocalDateTime timestamp;
    /**
     * The HTTP status code
     */
    private int status;
    /**
     * The error (HTTP status reason phrase)
     */
    private String error;
    /**
     * The standardized error code for frontend handling
     */
    private String code;
    /**
     * The message
     */
    private String message;
    /**
     * The path
     */
    private String path;
}
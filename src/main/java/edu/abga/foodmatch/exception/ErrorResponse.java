package edu.abga.foodmatch.exception;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Estructura estándar para las respuestas de error de la API.
 */
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

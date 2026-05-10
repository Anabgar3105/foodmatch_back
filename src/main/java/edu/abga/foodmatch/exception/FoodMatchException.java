package edu.abga.foodmatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling application-specific errors in the FoodMatch application.
 * <p>This exception is designed to carry both a descriptive error message and an associated HTTP status code</p>
 */
@Getter
public class FoodMatchException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructor for creating a new FoodMatchException with a specific error message and HTTP status code.
     *
     * @param message the error message that describes the reason for the exception, which will be included in the response sent to the client.
     * @param status  the HTTP status code that should be returned to the client when this exception is thrown.
     */
    public FoodMatchException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

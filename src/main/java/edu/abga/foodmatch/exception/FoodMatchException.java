package edu.abga.foodmatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling application-specific errors in the FoodMatch application.
 * <p>This exception is designed to carry a descriptive error message, an associated HTTP status code,
 * and a standardized error code for frontend error handling.</p>
 */
@Getter
public class FoodMatchException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    /**
     * Constructor for creating a new FoodMatchException with error code, message and HTTP status.
     *
     * @param errorCode the standardized error code for frontend handling
     * @param message   the error message that describes the reason for the exception
     * @param status    the HTTP status code that should be returned to the client
     */
    public FoodMatchException(ErrorCode errorCode, String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    /**
     * Constructor for backwards compatibility - uses error code's default message.
     *
     * @param errorCode the standardized error code
     * @param status    the HTTP status code
     */
    public FoodMatchException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.getUserMessage());
        this.status = status;
        this.errorCode = errorCode;
    }
}

package edu.abga.foodmatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepción base personalizada para el proyecto FoodMatch.
 * Permite definir un mensaje específico y el código de estado HTTP asociado.
 */
@Getter
public class FoodMatchException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Crea una nueva excepción personalizada.
     *
     * @param message Mensaje descriptivo del error para el usuario.
     * @param status  Código de estado HTTP del error.
     */
    public FoodMatchException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

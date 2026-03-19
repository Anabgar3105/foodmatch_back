package edu.abga.foodmatch.util;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;

/**
 * Clase de utilidad para centralizar las validaciones manuales de los DTOs.
 */
public class ValidationUtils {

    /**
     * Valida que los campos obligatorios del registro estén presentes y sean correctos.
     * @param dto DTO con los datos de registro.
     * @throws FoodMatchException si alguna validación falla.
     */
    public static void validateRegistrationData(UserRegistrationDto dto) {
        if (dto == null) {
            throw new FoodMatchException("El cuerpo de la petición no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        if (isNullOrBlank(dto.getName()) ||
                isNullOrBlank(dto.getEmail()) ||
                isNullOrBlank(dto.getUsername()) ||
                isNullOrBlank(dto.getPassword())) {
            throw new FoodMatchException("Debes completar todos los datos", HttpStatus.BAD_REQUEST);
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new FoodMatchException("El formato del email no es válido", HttpStatus.BAD_REQUEST);
        }

        validatePasswordStrength(dto.getPassword());
    }

    /**
     * Valida que la contraseña cumpla con los requisitos mínimos de seguridad.
     */
    public static void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new FoodMatchException("La contraseña debe tener al menos 8 caracteres", HttpStatus.BAD_REQUEST);
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new FoodMatchException("La contraseña debe contener al menos una letra mayúscula", HttpStatus.BAD_REQUEST);
        }
        if (!password.matches(".*\\d.*")) {
            throw new FoodMatchException("La contraseña debe contener al menos un número", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method auxiliar para comprobar si un String es nulo o está compuesto solo por espacios.
     */
    private static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty() || str.isBlank();
    }
}
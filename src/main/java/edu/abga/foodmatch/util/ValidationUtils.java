package edu.abga.foodmatch.util;

import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;

/**
 * Utility class to centralize manual DTO validations.
 */
public class ValidationUtils {

    /**
     * Validates that the mandatory registration fields are present and correct.
     * @param dto DTO with the registration data.
     * @throws FoodMatchException if any validation fails.
     */
    public static void validateRegistrationData(UserRegistrationDto dto) {
        if (dto == null) {
            throw new FoodMatchException(ErrorCode.MISSING_FIELD, "El cuerpo de la petición no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        if (isNullOrBlank(dto.getName()) ||
                isNullOrBlank(dto.getEmail()) ||
                isNullOrBlank(dto.getUsername()) ||
                isNullOrBlank(dto.getPassword())) {
            throw new FoodMatchException(ErrorCode.MISSING_FIELD, "Debes completar todos los datos", HttpStatus.BAD_REQUEST);
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new FoodMatchException(ErrorCode.INVALID_EMAIL, "El formato del email no es válido", HttpStatus.BAD_REQUEST);
        }

        validatePasswordStrength(dto.getPassword());
    }

    /**
     * Validates that the password meets the minimum security requirements.
     */
    public static void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new FoodMatchException(ErrorCode.WEAK_PASSWORD, "La contraseña debe tener al menos 8 caracteres", HttpStatus.BAD_REQUEST);
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new FoodMatchException(ErrorCode.WEAK_PASSWORD, "La contraseña debe contener al menos una letra mayúscula", HttpStatus.BAD_REQUEST);
        }
        if (!password.matches(".*\\d.*")) {
            throw new FoodMatchException(ErrorCode.WEAK_PASSWORD, "La contraseña debe contener al menos un número", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Helper method to check if a String is null or consists only of spaces.
     */
    private static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty() || str.isBlank();
    }
}
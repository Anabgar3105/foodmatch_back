package edu.abga.foodmatch.util;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias puras para la clase {@link ValidationUtils}
 */
public class ValidationUtilsTest {

    /**
     * Verifica el flujo de éxito de las validaciones.
     */
    @Test
    void validateRegistrationDataSuccess() {
        UserRegistrationDto validDto = UtilsForTests.registrationDto();
        assertDoesNotThrow(() -> ValidationUtils.validateRegistrationData(validDto));
    }

    /**
     * Verifica la correcta validación del formato del correo electrónico.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenEmailIsInvalid() {
        UserRegistrationDto invalidEmailDto = UtilsForTests.registrationDto();
        invalidEmailDto.setEmail("invalid-email");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> ValidationUtils.validateRegistrationData(invalidEmailDto));
        assertEquals("El formato del email no es válido", exception.getMessage());
    }

    /**
     * Verifica la correcta validación de la longitud mínima de la contraseña.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenPasswordIsTooShort() {
        UserRegistrationDto shortPasswordDto = UtilsForTests.registrationDto();
        shortPasswordDto.setPassword("short");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> ValidationUtils.validateRegistrationData(shortPasswordDto));
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    /**
     * Verifica la correcta validación de la presencia de al menos una mayúscula.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenPasswordLacksUppercase() {
        UserRegistrationDto noUppercaseDto = UtilsForTests.registrationDto();
        noUppercaseDto.setPassword("password1");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(noUppercaseDto));
        assertEquals("La contraseña debe contener al menos una letra mayúscula", exception.getMessage());
    }

    /**
     * Verifica la correcta validación de la presencia de al menos un caracter numérico.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenPasswordLacksNumber() {
        UserRegistrationDto noNumberDto = UtilsForTests.registrationDto();
        noNumberDto.setPassword("Password");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(noNumberDto));
        assertEquals("La contraseña debe contener al menos un número", exception.getMessage());
    }

    /**
     * Verifica la integridad de la petición general.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenDtoIsNull() {
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(null));
        assertEquals("El cuerpo de la petición no puede estar vacío", exception.getMessage());
    }

    /**
     * Verifica la protección contra campos obligatorios que contienen únicamente espacios.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenMandatoryFieldIsBlank() {
        UserRegistrationDto blankFieldsDto = UtilsForTests.registrationDto();
        blankFieldsDto.setName("   ");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(blankFieldsDto));
        assertEquals("Debes completar todos los datos", exception.getMessage());
    }

    /**
     * Verifica la protección contra objetos DTO incompletos.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenMandatoryFieldsAreMissing() {
        UserRegistrationDto incompleteDto = UserRegistrationDto.builder()
                .username("justme")
                .build();
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(incompleteDto));
        assertEquals("Debes completar todos los datos", exception.getMessage());
    }
}

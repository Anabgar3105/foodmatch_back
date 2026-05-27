package edu.abga.foodmatch.util;

import edu.abga.foodmatch.UtilsForTests;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for the {@link ValidationUtils} class
 */
public class ValidationUtilsTest {

    /**
     * Verifies the success flow of the validations.
     */
    @Test
    void validateRegistrationDataSuccess() {
        UserRegistrationDto validDto = UtilsForTests.registrationDto();
        assertDoesNotThrow(() -> ValidationUtils.validateRegistrationData(validDto));
    }

    /**
     * Verifies the correct validation of the email format.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenEmailIsInvalid() {
        UserRegistrationDto invalidEmailDto = UtilsForTests.registrationDto();
        invalidEmailDto.setEmail("invalid-email");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> ValidationUtils.validateRegistrationData(invalidEmailDto));
        assertEquals("El formato del email no es válido", exception.getMessage());
    }

    /**
     * Verifies the correct validation of the minimum password length.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenPasswordIsTooShort() {
        UserRegistrationDto shortPasswordDto = UtilsForTests.registrationDto();
        shortPasswordDto.setPassword("short");
        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> ValidationUtils.validateRegistrationData(shortPasswordDto));
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    /**
     * Verifies the correct validation of the presence of at least one uppercase letter.
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
     * Verifies the correct validation of the presence of at least one numeric character.
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
     * Verifies the integrity of the general request.
     */
    @Test
    void validateRegistrationDataThrowsExceptionWhenDtoIsNull() {
        FoodMatchException exception = assertThrows(FoodMatchException.class, () ->
                ValidationUtils.validateRegistrationData(null));
        assertEquals("El cuerpo de la petición no puede estar vacío", exception.getMessage());
    }

    /**
     * Verifies the protection against mandatory fields that contain only spaces.
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
     * Verifies the protection against incomplete DTO objects.
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

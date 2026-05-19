package edu.abga.foodmatch.util;

import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.*;

/**
 * Clase de utilidades para las pruebas unitarias.
 * Centraliza la creación de mocks y DTOs necesarios para los tests.
 */
public class UtilsForTests {

    /**
     * Genera un DTO de registro simulando los datos de entrada de un nuevo usuario.
     *
     * @return UserRegistrationDto.
     */
    public static UserRegistrationDto registrationDto() {
        return UserRegistrationDto.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .password("Secreta123")
                .build();
    }

    /**
     * Genera una entidad User simulando un registro ya persistido en la base de datos.
     *
     * @return User
     */
    public static User userEntity() {
        return User.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .password("hashedSecreta")
                .build();
    }

    /**
     * Genera un DTO de respuesta simulando la salida del sistema hacia el cliente.
     *
     * @return UserResponseDto
     */
    public static UserResponseDto userResponseDto() {
        return UserResponseDto.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .build();
    }

    /**
     * Genera un DTO de login simulando un intento de autenticación del usuario.
     *
     * @return UserLoginDto
     */
    public static UserLoginDto loginDto() {
        return UserLoginDto.builder()
                .username("d.redondo")
                .password("1234")
                .build();
    }

     /**
      * Genera un DTO de detalle de receta para pruebas de creación.
      * @return RecipeDetailDto
      */
     public static RecipeDetailDto recipeDetailDto() {
         return RecipeDetailDto.builder()
                 .title("Tortilla de Patatas")
                 .description("Receta clásica")
                 .preparationTime(30)
                 .category("PLATOS_COMPLETOS")
                 .build();
     }

    /**
     * Genera una entidad Recipe simulando que viene de la base de datos.
     * @return Recipe
     */
    public static Recipe recipeEntity() {
        return Recipe.builder()
                .id(1L)
                .title("Tortilla de Patatas")
                .description("Receta clásica")
                .preparationTime(30)
                .category(RecipeCategory.PLATOS_COMPLETOS)
                .ingredients(new java.util.ArrayList<>()) // Lista inicializada
                .steps(new java.util.ArrayList<>())       // Lista inicializada
                .build();
    }

    /**
     * Genera un DTO de tarjeta para las pruebas de búsqueda y listado.
     * @return RecipeCardDto
     */
    public static RecipeCardDto recipeCardDto() {
        return RecipeCardDto.builder()
                .id(1L)
                .title("Tortilla de Patatas")
                .preparationTime(30)
                .category("Cena")
                .build();
    }

    /**
     * Generates a UserUpdateDto simulating profile update data with default values.
     * @return UserUpdateDto
     */
    public static UserUpdateDto userUpdateDto() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("d.redondo.updated");
        dto.setEmail("dredondo.updated@email.com");
        dto.setAvatarUrl("https://avatar.updated.url");
        return dto;
    }

    /**
     * Generates a UserUpdateDto with specific data for testing profile updates.
     * @param username the new username
     * @param email the new email
     * @param avatarUrl the new avatar URL
     * @return UserUpdateDto with the specified data
     */
    public static UserUpdateDto userUpdateDto(String username, String email, String avatarUrl) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setAvatarUrl(avatarUrl);
        return dto;
    }

    /**
     * Generates a PasswordChangeDto simulating a password change request.
     * @return PasswordChangeDto
     */
    public static PasswordChangeDto passwordChangeDto() {
        PasswordChangeDto dto = new PasswordChangeDto();
        dto.setCurrentPassword("Secreta123");
        dto.setNewPassword("NewSecreta123");
        return dto;
    }

    /**
     * Generates a PasswordChangeDto with specific data for testing password changes.
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return PasswordChangeDto with the specified data
     */
    public static PasswordChangeDto passwordChangeDto(String currentPassword, String newPassword) {
        PasswordChangeDto dto = new PasswordChangeDto();
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        return dto;
    }
}

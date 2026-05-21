package edu.abga.foodmatch;

import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.*;

/**
 * Utility class for tests, providing methods to create mock objects and DTOs for testing purposes.
 * <p>This class contains static methods that generate instances of entities and DTOs with predefined data,
 * simulating real data that would be used in the application. These methods can be used across different test classes to ensure consistency and reduce code duplication when setting up test scenarios.</p>
 */
public class UtilsForTests {

    /**
     * Geenera un UserRegistrationDto simulating the data that a new user would provide when registering in the system.
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
     * Generates a User entity simulating a user already persisted in the database, with hashed password.
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
     * Generates a UserResponseDto simulating the data that would be returned to the client after a successful registration or login.
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
     * Generates a UserLoginDto simulating the login data that a user would send to the system when trying to log in.
     * @return UserLoginDto
     */
    public static UserLoginDto loginDto() {
        return UserLoginDto.builder()
                .username("d.redondo")
                .password("1234")
                .build();
    }

     /**
      * Generates a RecipeDetailDto simulating the data of a recipe that would be sent to the client when requesting the details of a recipe.
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
     * Generates a Recipe entity simulating a recipe already persisted in the database.
     * @return Recipe
     */
    public static Recipe recipeEntity() {
        return Recipe.builder()
                .id(1L)
                .title("Tortilla de Patatas")
                .description("Receta clásica")
                .preparationTime(30)
                .category(RecipeCategory.PLATOS_COMPLETOS)
                .ingredients(new java.util.ArrayList<>())
                .steps(new java.util.ArrayList<>())
                .build();
    }

    /**
     * Generates a RecipeCardDto simulating the data that would be sent to the client when requesting a list of recipes or a recipe card.
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

package edu.abga.foodmatch.service;

import edu.abga.foodmatch.model.Recipe;
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
                .password("1234")
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
                .category("Cena")
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
                .category("Cena")
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
}

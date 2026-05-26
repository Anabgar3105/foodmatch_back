package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.mapper.RecipeMapper;
import edu.abga.foodmatch.repository.RecipeRepository;
import edu.abga.foodmatch.repository.UserRepository;
import edu.abga.foodmatch.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link FavoriteService} class.
 * Verifies the business logic for adding, removing, and retrieving favorite recipes (Matches).
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private FavoriteService favoriteService;

    /**
     * Verifies that a recipe is successfully added to the user's favorites
     * when both the user and the recipe exist and are accessible.
     */
    @Test
    void addFavoriteSuccess() {
        Long userId = 1L;
        Long recipeId = 1L;
        User mockUser = UtilsForTests.userEntity();
        Recipe mockRecipe = UtilsForTests.recipeEntity();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findRecipeById(recipeId, userId)).thenReturn(Optional.of(mockRecipe));

        favoriteService.addFavorite(userId, recipeId);

        assertTrue(mockUser.getFavouriteRecipes().contains(mockRecipe));
        verify(userRepository).save(mockUser);
    }

    /**
     * Verifies that a {@link FoodMatchException} is thrown with a 404 status
     * when attempting to add a favorite if the recipe does not exist or the user
     * does not have access permissions.
     */
    @Test
    void addFavoriteThrowsExceptionWhenRecipeNotFoundOrPrivate() {
        Long userId = 1L;
        Long recipeId = 1L;
        User mockUser = UtilsForTests.userEntity();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findRecipeById(recipeId, userId)).thenReturn(Optional.empty());

        FoodMatchException exception = assertThrows(FoodMatchException.class,
                () -> favoriteService.addFavorite(userId, recipeId));

        assertEquals("La receta no existe", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that a recipe is successfully removed from the user's list of favorites.
     */
    @Test
    void removeFavoriteSuccess() {
        Long userId = 1L;
        Long recipeId = 1L;
        User mockUser = UtilsForTests.userEntity();
        Recipe mockRecipe = UtilsForTests.recipeEntity();
        mockUser.getFavouriteRecipes().add(mockRecipe); // Añadimos primero para borrarla luego

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));

        favoriteService.removeFavorite(userId, recipeId);

        assertFalse(mockUser.getFavouriteRecipes().contains(mockRecipe));
        verify(userRepository).save(mockUser);
    }

    /**
     * Verifies that the service retrieves the correct list of favorite recipes
     * mapped to {@link RecipeCardDto} for a specific user.
     */
    @Test
    void getUserFavoritesReturnsCardList() {
        Long userId = 1L;
        User mockUser = UtilsForTests.userEntity();
        mockUser.getFavouriteRecipes().add(UtilsForTests.recipeEntity());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> favorites = favoriteService.getUserFavorites(userId);

        assertFalse(favorites.isEmpty());
        assertEquals(1, favorites.size());
        verify(recipeMapper, times(1)).toCardDto(any(Recipe.class));
    }
}
package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.model.mapper.RecipeMapper;
import edu.abga.foodmatch.repository.RecipeRepository;
import edu.abga.foodmatch.repository.UserRepository;
import edu.abga.foodmatch.util.UtilsForTests;
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
 * Unit test suite for {@link RecipeService}.
 * Verifies the business logic, input validations, and the different
 * filtering flows of the recipe catalog.
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeService recipeService;

    /**
     * Verifies that the creation of a recipe is completed successfully when
     * valid data is provided. It ensures that the repository is invoked correctly
     * and that the output DTO contains the expected information.
     */
    @Test
    void createRecipeSuccess() {
        RecipeDetailDto inputDto = UtilsForTests.recipeDetailDto();
        Recipe mockEntity = UtilsForTests.recipeEntity();

        when(recipeMapper.toEntity(any(RecipeDetailDto.class))).thenReturn(mockEntity);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockEntity);
        when(recipeMapper.toDetailDto(any(Recipe.class))).thenReturn(inputDto);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(UtilsForTests.userEntity()));

        RecipeDetailDto result = recipeService.createRecipe(inputDto, UtilsForTests.userEntity().getUsername());

        assertNotNull(result);
        assertEquals("Tortilla de Patatas", result.getTitle());
        verify(recipeRepository).save(any(Recipe.class));
    }

    /**
     * Verifies that the service throws an exception with the correct message and status code (400 Bad Request)
     * when trying to create a recipe without a title. It also ensures that
     * the repository is not invoked to save the entity.
     */
    @Test
    void createRecipeThrowsExceptionWhenTitleIsMissing() {
        RecipeDetailDto invalidDto = new RecipeDetailDto();
        invalidDto.setTitle("");

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> recipeService.createRecipe(invalidDto, ""));

        assertEquals("El título de la receta es obligatorio", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    /**
     * Verifies the validation that prevents setting a preparation time
     * with a negative value. It checks that the corresponding exception is thrown
     * and the save is aborted.
     */
    @Test
    void createRecipeThrowsExceptionWhenTimeIsNegative() {
        RecipeDetailDto invalidDto = UtilsForTests.recipeDetailDto();
        invalidDto.setPreparationTime(-10);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> recipeService.createRecipe(invalidDto, ""));

        assertEquals("El tiempo de preparación no puede ser negativo", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    /**
     * Checks that the search engine correctly applies the filter by category,
     * delegating to the custom method of the repository and mapping
     * the result to a CardDto.
     */
    @Test
    void searchRecipesByCategorySuccess() {
        RecipeCategory targetCategory = RecipeCategory.PLATOS_COMPLETOS;
        List<Recipe> mockDbResult = List.of(UtilsForTests.recipeEntity());

        when(recipeRepository.findByCategory(targetCategory,1L)).thenReturn(mockDbResult);
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> results = recipeService.searchRecipes(targetCategory, null,1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Tortilla de Patatas", results.get(0).getTitle());
        verify(recipeRepository).findByCategory(targetCategory,1L);
    }

    /**
     * Checks that the search engine correctly applies the maximum time filter,
     * isolating the search from other parameters such as the category.
     */
    @Test
    void searchRecipesByMaxTimeSuccess() {
        Integer maxTime = 30;
        List<Recipe> mockDbResult = List.of(UtilsForTests.recipeEntity());

        when(recipeRepository.findByMaxTime(maxTime,1L)).thenReturn(mockDbResult);
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> results = recipeService.searchRecipes(null, maxTime,1L);

        assertFalse(results.isEmpty());
        verify(recipeRepository).findByMaxTime(maxTime,1L);
    }

    /**
     * Verifies that when invoking the retrieval of the catalog for a specific user,
     * the service retrieves the public records and the user's private records.
     */
    @Test
    void getRecipesForUserReturnsFilteredList() {
        Long testUserId = 1L;

        when(recipeRepository.findRecipes(testUserId))
                .thenReturn(List.of(UtilsForTests.recipeEntity()));
        when(recipeMapper.toDetailDto(any(Recipe.class)))
                .thenReturn(UtilsForTests.recipeDetailDto());

        List<RecipeDetailDto> results = recipeService.getRecipesForUser(testUserId);

        assertEquals(1, results.size());
        verify(recipeRepository).findRecipes(testUserId);
    }

    /**
     * Verifies that the service retrieves the recipe detail when the user
     * has permissions (public recipe or owner).
     */
    @Test
    void getRecipeByIdReturnsDetailDtoWhenFound() {
        Long recipeId = 1L;
        Long userId = 1L;
        Recipe mockEntity = UtilsForTests.recipeEntity();

        when(recipeRepository.findRecipeById(recipeId, userId)).thenReturn(java.util.Optional.of(mockEntity));
        when(recipeMapper.toDetailDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeDetailDto());

        RecipeDetailDto result = recipeService.getRecipeById(recipeId, userId);

        assertNotNull(result);
        assertEquals("Tortilla de Patatas", result.getTitle());
        verify(recipeRepository).findRecipeById(recipeId, userId);
    }

    /**
     * Verifies that a 404 Not Found exception is thrown when the recipe
     * does not exist or belongs to another user.
     */
    @Test
    void getRecipeByIdThrowsExceptionWhenNotFoundOrPrivate() {
        Long recipeId = 1L;
        Long userId = 1L;

        when(recipeRepository.findRecipeById(recipeId, userId)).thenReturn(java.util.Optional.empty());

        FoodMatchException exception = assertThrows(FoodMatchException.class,
                () -> recipeService.getRecipeById(recipeId, userId));

        assertEquals("Receta no encontrada", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    /**
     * Verifies that a recipe is successfully deleted when the requesting user
     * is the original creator (owner) of the recipe.
     */
    @Test
    void deleteRecipeSuccessWhenUserIsOwner() {
        Long recipeId = 1L;
        Long currentUserId = 1L;

        User mockOwner = UtilsForTests.userEntity();
        mockOwner.setId(currentUserId);

        Recipe mockRecipe = UtilsForTests.recipeEntity();
        mockRecipe.setUser(mockOwner);

        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.of(mockRecipe));
        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(mockOwner));

        assertDoesNotThrow(() -> recipeService.deleteRecipe(recipeId, currentUserId));
        verify(recipeRepository).delete(mockRecipe);
    }

    /**
     * Verifies that a recipe (even a public one without an owner) is successfully
     * deleted when the requesting user has the ADMIN role.
     */
    @Test
    void deleteRecipeSuccessWhenUserIsAdmin() {
        Long recipeId = 1L;
        Long currentUserId = 2L;

        User mockAdmin = UtilsForTests.userEntity();
        mockAdmin.setId(currentUserId);
        mockAdmin.setRole(edu.abga.foodmatch.model.Role.ADMIN); // Le damos rol de Admin

        Recipe mockPublicRecipe = UtilsForTests.recipeEntity();
        mockPublicRecipe.setUser(null); // Receta pública (sin dueño)

        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.of(mockPublicRecipe));
        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(mockAdmin));

        assertDoesNotThrow(() -> recipeService.deleteRecipe(recipeId, currentUserId));
        verify(recipeRepository).delete(mockPublicRecipe);
    }

    /**
     * Verifies that a 403 Forbidden exception is thrown and the deletion is aborted
     * when a standard user tries to delete a recipe they do not own (e.g., a public recipe).
     */
    @Test
    void deleteRecipeThrowsExceptionWhenNotOwnerOrAdmin() {
        Long recipeId = 1L;
        Long currentUserId = 1L;

        User mockStandardUser = UtilsForTests.userEntity();
        mockStandardUser.setId(currentUserId);
        mockStandardUser.setRole(edu.abga.foodmatch.model.Role.USER);

        Recipe mockPublicRecipe = UtilsForTests.recipeEntity();
        mockPublicRecipe.setUser(null); // Receta pública, no es suya

        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.of(mockPublicRecipe));
        when(userRepository.findById(currentUserId)).thenReturn(java.util.Optional.of(mockStandardUser));

        FoodMatchException exception = assertThrows(FoodMatchException.class,
                () -> recipeService.deleteRecipe(recipeId, currentUserId));

        assertEquals("No tienes permisos para borrar esta receta", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    /**
     * Verifies that the service successfully retrieves all recipes created by a specific user.
     * Ensures that the repository is called with the correct user ID and the DTOs are correctly mapped.
     */
    @Test
    void getMyRecipesReturnsListOfRecipeCardsForUser() {
        String username = "d.redondo";
        User mockUser = UtilsForTests.userEntity();
        mockUser.setId(1L);
        mockUser.setUsername(username);

        List<Recipe> mockRecipes = List.of(UtilsForTests.recipeEntity());

        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(recipeRepository.findByUserId(mockUser.getId())).thenReturn(mockRecipes);
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> results = recipeService.getMyRecipes(username);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Tortilla de Patatas", results.get(0).getTitle());
        verify(userRepository).findByUsername(username);
        verify(recipeRepository).findByUserId(mockUser.getId());
    }

    /**
     * Verifies that the service throws a 404 Not Found exception when trying to retrieve
     * recipes for a user that does not exist in the system.
     */
    @Test
    void getMyRecipesThrowsExceptionWhenUserNotFound() {
        String username = "nonexistent.user";

        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());

        FoodMatchException exception = assertThrows(FoodMatchException.class,
                () -> recipeService.getMyRecipes(username));

        assertEquals("Usuario no encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(recipeRepository, never()).findByUserId(any());
    }

    /**
     * Verifies that the service returns an empty list when a user has no recipes.
     */
    @Test
    void getMyRecipesReturnsEmptyListWhenUserHasNoRecipes() {
        String username = "d.redondo";
        User mockUser = UtilsForTests.userEntity();
        mockUser.setId(1L);
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        when(recipeRepository.findByUserId(mockUser.getId())).thenReturn(List.of());

        List<RecipeCardDto> results = recipeService.getMyRecipes(username);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(userRepository).findByUsername(username);
        verify(recipeRepository).findByUserId(mockUser.getId());
    }
}
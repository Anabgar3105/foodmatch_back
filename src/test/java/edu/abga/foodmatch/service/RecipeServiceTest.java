package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.model.mapper.RecipeMapper;
import edu.abga.foodmatch.repository.RecipeRepository;
import edu.abga.foodmatch.util.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

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

        RecipeDetailDto result = recipeService.createRecipe(inputDto);

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

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> recipeService.createRecipe(invalidDto));

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

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> recipeService.createRecipe(invalidDto));

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
        String targetCategory = "Cena";
        List<Recipe> mockDbResult = List.of(UtilsForTests.recipeEntity());

        when(recipeRepository.findByCategoryIgnoreCase(targetCategory)).thenReturn(mockDbResult);
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> results = recipeService.searchRecipes(targetCategory, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Tortilla de Patatas", results.get(0).getTitle());
        verify(recipeRepository).findByCategoryIgnoreCase(targetCategory);
    }

    /**
     * Checks that the search engine correctly applies the maximum time filter,
     * isolating the search from other parameters such as the category.
     */
    @Test
    void searchRecipesByMaxTimeSuccess() {
        Integer maxTime = 30;
        List<Recipe> mockDbResult = List.of(UtilsForTests.recipeEntity());

        when(recipeRepository.findByPreparationTimeLessThanEqual(maxTime)).thenReturn(mockDbResult);
        when(recipeMapper.toCardDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeCardDto());

        List<RecipeCardDto> results = recipeService.searchRecipes(null, maxTime);

        assertFalse(results.isEmpty());
        verify(recipeRepository).findByPreparationTimeLessThanEqual(maxTime);
    }

    /**
     * Verifies that when invoking the retrieval of the complete catalog,
     * the service retrieves all records without applying filters.
     */
    @Test
    void getAllRecipesReturnsFullList() {
        when(recipeRepository.findAll()).thenReturn(List.of(UtilsForTests.recipeEntity()));
        when(recipeMapper.toDetailDto(any(Recipe.class))).thenReturn(UtilsForTests.recipeDetailDto());

        List<RecipeDetailDto> results = recipeService.getAllRecipes();

        assertEquals(1, results.size());
        verify(recipeRepository).findAll();
    }
}
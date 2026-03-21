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
 * Suite de pruebas unitarias para {@link RecipeService}.
 * Verifica la lógica de negocio, las validaciones de entrada y los diferentes
 * flujos de filtrado del catálogo de recetas.
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
     * Verifica que la creación de una receta se complete exitosamente cuando se
     * proporcionan datos válidos. Se asegura que el repositorio se invoque correctamente
     * y que el DTO de salida contenga la información esperada.
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
     * Verifica que el servicio lance una excepción con el mensaje y código de estado (400 Bad Request)
     * correctos cuando se intenta crear una receta sin título. Además, se asegura que
     * el repositorio no se invoque para guardar la entidad.
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
     * Verifica la validación que impide establecer un tiempo de preparación
     * con valor negativo. Comprueba que se lance la excepción correspondiente
     * y se aborte el guardado.
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
     * Comprueba que el buscador aplica correctamente el filtro por categoría,
     * delegando en el method personalizado del repositorio y mapeando
     * el resultado a un CardDto.
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
     * Comprueba que el buscador aplica correctamente el filtro de tiempo máximo,
     * aislando la búsqueda de otros parámetros como la categoría.
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
     * Verifica que al invocar la obtención del catálogo completo,
     * el servicio recupera todos los registros sin aplicar filtros.
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
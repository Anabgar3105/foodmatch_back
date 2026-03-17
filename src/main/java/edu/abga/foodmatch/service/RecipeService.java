package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.model.mapper.RecipeMapper;
import edu.abga.foodmatch.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de recetas.
 */
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    /**
     * Guarda una nueva receta validando los datos de entrada.
     * @throws FoodMatchException si los datos de la receta son inválidos.
     */
    @Transactional
    public RecipeDetailDto createRecipe(RecipeDetailDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new FoodMatchException("El título de la receta es obligatorio", HttpStatus.BAD_REQUEST);
        }

        if (dto.getPreparationTime() != null && dto.getPreparationTime() < 0) {
            throw new FoodMatchException("El tiempo de preparación no puede ser negativo", HttpStatus.BAD_REQUEST);
        }

        Recipe recipe = recipeMapper.toEntity(dto);

        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
        }
        if (recipe.getSteps() != null) {
            recipe.getSteps().forEach(step -> step.setRecipe(recipe));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDetailDto(savedRecipe);
    }

    /**
     * Obtiene todas las recetas registradas.
     */
    @Transactional(readOnly = true)
    public List<RecipeDetailDto> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca recetas aplicando filtros opcionales.
     * @param category Categoría exacta (ej. "Cena"). Puede ser null.
     * @param maxTime Tiempo máximo en minutos. Puede ser null.
     * @return Lista de recetas en formato tarjeta (más ligero).
     */
    @Transactional(readOnly = true)
    public List<RecipeCardDto> searchRecipes(String category, Integer maxTime) {
        List<Recipe> entities;

        if (category != null && maxTime != null) {
            entities = recipeRepository.findByCategoryIgnoreCase(category).stream()
                    .filter(r -> r.getPreparationTime() != null && r.getPreparationTime() <= maxTime)
                    .collect(Collectors.toList());
        } else if (category != null) {
            entities = recipeRepository.findByCategoryIgnoreCase(category);
        } else if (maxTime != null) {
            entities = recipeRepository.findByPreparationTimeLessThanEqual(maxTime);
        } else {
            entities = recipeRepository.findAll();
        }

        return entities.stream()
                .map(recipeMapper::toCardDto)
                .collect(Collectors.toList());
    }
}
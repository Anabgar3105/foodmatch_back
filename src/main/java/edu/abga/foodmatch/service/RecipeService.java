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
 * Service for recipe management.
 */
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    /**
     * Saves a new recipe, validating the input data.
     * @throws FoodMatchException if the recipe data is invalid.
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
     * Gets all registered recipes.
     */
    @Transactional(readOnly = true)
    public List<RecipeDetailDto> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Searches for recipes applying optional filters.
     * @param category Exact category. Can be null.
     * @param maxTime Maximum time in minutes. Can be null.
     * @return List of recipes in card format.
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
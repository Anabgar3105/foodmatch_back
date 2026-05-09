package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.Role;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.model.mapper.RecipeMapper;
import edu.abga.foodmatch.repository.RecipeRepository;
import edu.abga.foodmatch.repository.UserRepository;
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
    private final UserRepository userRepository;

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
     * Returns public recipes and recipes created by the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<RecipeDetailDto> getRecipesForUser(Long currentUserId) {
        List<Recipe> recipes = recipeRepository.findRecipes(currentUserId);
        return recipes.stream()
                .map(recipeMapper::toDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Searches for recipes applying optional filters, respecting user privacy.
     * @param category Exact category. Can be null.
     * @param maxTime Maximum time in minutes. Can be null.
     * @param currentUserId The ID of the authenticated user making the request.
     * @return List of accessible recipes in card format.
     */
    @Transactional(readOnly = true)
    public List<RecipeCardDto> searchRecipes(RecipeCategory category, Integer maxTime, Long currentUserId) {
        List<Recipe> entities;

        if (category != null && maxTime != null) {
            entities = recipeRepository.findByCategory(category, currentUserId).stream()
                    .filter(r -> r.getPreparationTime() != null && r.getPreparationTime() <= maxTime)
                    .collect(Collectors.toList());
        } else if (category != null) {
            entities = recipeRepository.findByCategory(category, currentUserId);
        } else if (maxTime != null) {
            entities = recipeRepository.findByMaxTime(maxTime, currentUserId);
        } else {
            entities = recipeRepository.findRecipes(currentUserId);
        }

        return entities.stream()
                .map(recipeMapper::toCardDto)
                .collect(Collectors.toList());
    }
    /**
     * Get a specific recipe by id
     * @param id of the recipe to retrieve
     * @param currentUserId The ID of the authenticated user making the request.
     * @return RecipeDetailDto with the complete information of the recipe.
     * @throws FoodMatchException if the recipe is not found or the user does not have permissions to view it.
     */
    @Transactional(readOnly = true)
    public RecipeDetailDto getRecipeById(Long id, Long currentUserId) {
        Recipe recipe = recipeRepository.findRecipeById(id, currentUserId)
                .orElseThrow(() -> new FoodMatchException("Receta no encontrada", HttpStatus.NOT_FOUND));

        return recipeMapper.toDetailDto(recipe);
    }

    /**
     * Deletes a recipe
     * @param recipeId the id of the recipe to delete
     * @param currentUserId The ID of the authenticated user making the request
     * @throws FoodMatchException if the recipe is not found or the user does not have permissions to view it.
     */
    @Transactional
    public void deleteRecipe(Long recipeId, Long currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new FoodMatchException("Receta no encontrada", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new FoodMatchException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        boolean isOwner = recipe.getUser() != null && recipe.getUser().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new FoodMatchException("No tienes permisos para borrar esta receta", HttpStatus.FORBIDDEN);
        }

        recipeRepository.delete(recipe);
    }
}
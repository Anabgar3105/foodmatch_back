package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.*;
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
    public RecipeDetailDto createRecipe(RecipeDetailDto dto, String username) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new FoodMatchException(ErrorCode.INVALID_INPUT, "El título de la receta es obligatorio", HttpStatus.BAD_REQUEST);
        }

        if (dto.getPreparationTime() != null && dto.getPreparationTime() < 0) {
            throw new FoodMatchException(ErrorCode.INVALID_INPUT, "El tiempo de preparación no puede ser negativo", HttpStatus.BAD_REQUEST);
        }

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Recipe recipe = recipeMapper.toEntity(dto);
        recipe.setUser(creator);

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
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        boolean isOwner = recipe.getUser() != null && recipe.getUser().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new FoodMatchException(ErrorCode.INSUFFICIENT_PERMISSIONS, "No tienes permisos para borrar esta receta", HttpStatus.FORBIDDEN);
        }

        recipeRepository.delete(recipe);
    }

    /**
     * Updates the image of a recipe, validating permissions.
     * @param recipeId the recipe to update
     * @param imageUrl the image URL to set
     * @param currentUserId the current authenticated user ID
     * @return RecipeDetailDto with the updated recipe information.
     * @throws FoodMatchException if the recipe is not found, the user is not found, or the user does not have permissions to edit the recipe.
     */
    @Transactional
    public RecipeDetailDto updateRecipeImage(Long recipeId, String imageUrl, Long currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!recipe.getUser().getId().equals(currentUserId) && currentUser.getRole() != Role.ADMIN) {
            throw new FoodMatchException(ErrorCode.INSUFFICIENT_PERMISSIONS, "No tienes permiso para editar esta receta", HttpStatus.FORBIDDEN);
        }

        recipe.setImage(imageUrl);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDetailDto(savedRecipe);
    }

    /**
     * Gets the recipes created by a specific user
     * @param username the username of the user whose recipes we want to retrieve
     * @return a list of RecipeCardDto representing the recipes created by the user.
     */
    public List<RecipeCardDto> getMyRecipes(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<Recipe> myRecipes = recipeRepository.findByUserId(user.getId());

        return myRecipes.stream()
                .map(recipeMapper::toCardDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Updates an existing recipe, validating permissions and input data.
     * @param recipeId the id of the recipe to update
     * @param recipeDto the new data for the recipe
     * @param username the username of the authenticated user making the request
     * @return RecipeDetailDto with the updated recipe information.
     * @throws FoodMatchException if the recipe is not found, the user is not found
     */
    @Transactional
    public RecipeDetailDto updateRecipe(Long recipeId, RecipeDetailDto recipeDto, String username) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (recipe.getUser() == null || !recipe.getUser().getUsername().equals(username)) {
            throw new FoodMatchException(ErrorCode.INSUFFICIENT_PERMISSIONS, "No tienes permisos para editar esta receta", HttpStatus.FORBIDDEN);
        }

        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setPreparationTime(recipeDto.getPreparationTime());
        if (recipeDto.getCategory() != null) {
            recipe.setCategory(edu.abga.foodmatch.model.RecipeCategory.valueOf(recipeDto.getCategory().toUpperCase()));
        }

        if (recipeDto.getImage() != null && !recipeDto.getImage().isEmpty()) {
            recipe.setImage(recipeDto.getImage());
        }

        recipe.getIngredients().clear();
        if (recipeDto.getIngredients() != null) {
            recipeDto.getIngredients().forEach(ingDto -> {
                Ingredient ing = new Ingredient();
                ing.setName(ingDto.getName());
                ing.setQuantity(ingDto.getQuantity());
                ing.setRecipe(recipe);
                recipe.getIngredients().add(ing);
            });
        }

        recipe.getSteps().clear();
        if (recipeDto.getSteps() != null) {
            recipeDto.getSteps().forEach(stepDto -> {
                ElaborationStep step = new ElaborationStep();
                step.setStepNum(stepDto.getStepNum());
                step.setInstruction(stepDto.getInstruction());
                step.setRecipe(recipe);
                recipe.getSteps().add(step);
            });
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toDetailDto(updatedRecipe);
    }
}
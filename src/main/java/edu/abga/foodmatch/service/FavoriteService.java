package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
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
 * Service to manage the "Match" (Favourites) logic between Users and Recipes.
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    /**
     * Adds a recipe to the user's list of favorites.
     * @param userId The ID of the user who wants to favorite the recipe.
     * @param recipeId The ID of the recipe to be favorited.
     */
    @Transactional
    public void addFavorite(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Recipe recipe = recipeRepository.findRecipeById(recipeId, userId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        user.getFavouriteRecipes().add(recipe);
        userRepository.save(user);
    }

    /**
     * Removes a recipe from the user's list of favorites.
     * @param userId The ID of the user who wants to remove the favorite.
     * @param recipeId The ID of the recipe to be removed from favorites.
     */
    @Transactional
    public void removeFavorite(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.RECIPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        user.getFavouriteRecipes().remove(recipe);
        userRepository.save(user);
    }

    /**
     * Retrieves all favorited recipes for a specific user.
     * @param userId The ID of the user whose favorites we want to retrieve.
     * @return A list of RecipeCardDto representing the user's favorite recipes.
     */
    @Transactional(readOnly = true)
    public List<RecipeCardDto> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return user.getFavouriteRecipes().stream()
                .map(recipeMapper::toCardDto)
                .collect(Collectors.toList());
    }
}
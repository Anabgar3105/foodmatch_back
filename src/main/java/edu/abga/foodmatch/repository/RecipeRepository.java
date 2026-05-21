package edu.abga.foodmatch.repository;

import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the Recipe entity.
 * Provides methods for CRUD operations and custom queries
 * on the recipes table in the FoodMatch database.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds all recipes that belong to a specific user ID.
     * @param userId the ID of the user whose recipes we want to find
     * @return a list of recipes that belong to the specified user ID
     */
    List<Recipe> findByUserId(Long userId);

    /**
     * Finds public recipes (where user is null) or recipes that belong to the specified user ID.
     * @param userId the ID of the user whose recipes we want to find, or null for public recipes
     * @return a list of recipes that are either public or belong to the specified user ID
     */
    @Query("SELECT r FROM Recipe r WHERE r.user IS NULL OR r.user.id = :userId")
    List<Recipe> findRecipes(@Param("userId") Long userId);

    /**
     * Finds a specific recipe by id as long as is public or belongs to the specific user.
     * @param id the ID of the recipe we want to find
     * @param userId the ID of the user who owns the recipe, or null for public recipes
     * @return an Optional containing the recipe if found, or empty if not found
     */
    @Query("SELECT r FROM Recipe r WHERE r.id = :id AND (r.user IS NULL OR r.user.id = :userId)")
    Optional<Recipe> findRecipeById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Finds recipes by category, ensuring that the recipe is either public or belongs to the specified user.
     * @param category the category of the recipes we want to find
     * @param userId the ID of the user who owns the recipe, or null for public recipes
     * @return a list of recipes that match the specified category and are either public or belong to the specified user
     */
    @Query("SELECT r FROM Recipe r WHERE r.category = :category AND (r.user IS NULL OR r.user.id = :userId)")
    List<Recipe> findByCategory(@Param("category") RecipeCategory category, @Param("userId") Long userId);

    /**
     * Finds recipes that have a preparation time less than or equal to the specified maximum, ensuring that the recipe is either public or belongs to the specified user.
     * @param maxMinutes the maximum preparation time in minutes
     * @param userId the ID of the user who owns the recipe, or null for public recipes
     * @return a list of recipes that have a preparation time less than or equal to the specified maximum and are either public or belong to the specified user
     */
    @Query("SELECT r FROM Recipe r WHERE r.preparationTime <= :maxMinutes AND (r.user IS NULL OR r.user.id = :userId)")
    List<Recipe> findByMaxTime(@Param("maxMinutes") Integer maxMinutes, @Param("userId") Long userId);
}
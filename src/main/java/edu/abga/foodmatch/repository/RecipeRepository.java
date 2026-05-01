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
     * Finds public recipes (where user is null) or recipes that belong to the specified user ID.
     */
    @Query("SELECT r FROM Recipe r WHERE r.user IS NULL OR r.user.id = :userId")
    List<Recipe> findRecipes(@Param("userId") Long userId);

    /**
     * Finds a specific recipe by id as long as is public or belongs to the specific user.
     */
    @Query("SELECT r FROM Recipe r WHERE r.id = :id AND (r.user IS NULL OR r.user.id = :userId)")
    Optional<Recipe> findRecipeById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Busca por categoría asegurando que la receta sea pública o del usuario.
     */
    @Query("SELECT r FROM Recipe r WHERE r.category = :category AND (r.user IS NULL OR r.user.id = :userId)")
    List<Recipe> findByCategory(@Param("category") RecipeCategory category, @Param("userId") Long userId);

    /**
     * Busca por tiempo máximo asegurando que la receta sea pública o del usuario.
     */
    @Query("SELECT r FROM Recipe r WHERE r.preparationTime <= :maxMinutes AND (r.user IS NULL OR r.user.id = :userId)")
    List<Recipe> findByMaxTime(@Param("maxMinutes") Integer maxMinutes, @Param("userId") Long userId);
}
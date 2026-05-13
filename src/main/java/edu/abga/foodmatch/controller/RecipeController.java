package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.service.RecipeService;
import edu.abga.foodmatch.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the recipe catalog.
 * Provides endpoints for creating, querying, and filtering recipes.
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "Endpoints para la gestión del catálogo de recetas")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Endpoint for creating a new recipe.
     * Saves the recipe along with its associated lists (ingredients and elaboration steps).
     *
     * @param dto with the complete information of the recipe.
     * @return ResponseEntity with the DTO of the created recipe and HTTP status 201.
     */
    @PostMapping
    @Operation(summary = "Crear receta", description = "Guarda una receta con sus ingredientes y pasos")
    public ResponseEntity<RecipeDetailDto> create(@RequestBody RecipeDetailDto dto) {
        return new ResponseEntity<>(recipeService.createRecipe(dto), HttpStatus.CREATED);
    }

    /**
     * Endpoint to get the complete catalog of recipes.
     *
     * @return List<RecipeDetailDto> with the details of all recipes registered in the system.
     */
    @GetMapping
    @Operation(summary = "Listar recetas", description = "Obtiene todas las recetas disponibles")
    public List<RecipeDetailDto> getAll() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return recipeService.getRecipesForUser(currentUserId);
    }

    /**
     * Endpoint to retrieve the complete details of a specific recipe.
     * Uses the authenticated user's ID to ensure privacy (only public or owned recipes are returned).
     *
     * @param id The unique identifier of the recipe.
     * @return ResponseEntity with the complete RecipeDetailDto.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de receta", description = "Obtiene toda la información (ingredientes y pasos) de una receta específica")
    public ResponseEntity<RecipeDetailDto> getRecipeById(@PathVariable("id") Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        RecipeDetailDto recipeDetail = recipeService.getRecipeById(id, currentUserId);

        return ResponseEntity.ok(recipeDetail);
    }

    /**
     * Endpoint to search and filter recipes in the catalog.
     * Allows combining optional search parameters.
     *
     * @param category Category of the recipe to search for. (Optional)
     * @param maxTime Maximum preparation time in minutes. (Optional)
     * @return ResponseEntity with a list of DTOs in card format that meet the criteria.
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar recetas", description = "Filtra recetas por categoría o por tiempo de preparación máximo")
    public ResponseEntity<List<RecipeCardDto>> searchRecipes(
            @RequestParam(required = false) RecipeCategory category,
            @RequestParam(required = false) Integer maxTime) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<RecipeCardDto> results = recipeService.searchRecipes(category, maxTime, currentUserId);

        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint to delete a specific recipe.
     * Requires ownership of the recipe or ADMIN role.
     *
     * @param id The unique identifier of the recipe to delete.
     * @return ResponseEntity with HTTP status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar receta", description = "Elimina una receta si el usuario es su creador o si tiene rol de administrador")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        recipeService.deleteRecipe(id, currentUserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Update the image URL of an existing recipe.
     * @param id the ID of the recipe to update.
     * @param body the request body containing the new image URL in the format: {"url": "new_image_url"}
     * @return ResponseEntity with the updated RecipeDetailDto.
     */
    @PatchMapping("/{id}/image")
    @Operation(summary = "Actualizar imagen de receta", description = "Actualiza la URL de la imagen de una receta existente")
    public ResponseEntity<RecipeDetailDto> updateImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String imageUrl = body.get("url");
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new FoodMatchException("La URL de la imagen es obligatoria", HttpStatus.BAD_REQUEST);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(recipeService.updateRecipeImage(id, imageUrl, currentUserId));
    }
}
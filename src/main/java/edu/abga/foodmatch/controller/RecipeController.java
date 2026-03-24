package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return recipeService.getAllRecipes();
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxTime) {

        List<RecipeCardDto> results = recipeService.searchRecipes(category, maxTime);
        return ResponseEntity.ok(results);
    }
}
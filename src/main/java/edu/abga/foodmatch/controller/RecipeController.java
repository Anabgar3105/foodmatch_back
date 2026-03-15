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
 * Controlador REST para la gestión del catálogo de recetas.
 * Proporciona los endpoints para crear, consultar y filtrar recetas.
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "Endpoints para la gestión del catálogo de recetas")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Endpoint para la creación de una nueva receta.
     * Guarda la receta junto con sus listas asociadas (ingredientes y pasos de elaboración).
     *
     * @param dto con la información completa de la receta.
     * @return ResponseEntity con el DTO de la receta creada y el estado HTTP 201.
     */
    @PostMapping
    @Operation(summary = "Crear receta", description = "Guarda una receta con sus ingredientes y pasos")
    public ResponseEntity<RecipeDetailDto> create(@RequestBody RecipeDetailDto dto) {
        return new ResponseEntity<>(recipeService.createRecipe(dto), HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener el catálogo completo de recetas.
     *
     * @return List<RecipeDetailDto> con el detalle de todas las recetas registradas en el sistema.
     */
    @GetMapping
    @Operation(summary = "Listar recetas", description = "Obtiene todas las recetas disponibles")
    public List<RecipeDetailDto> getAll() {
        return recipeService.getAllRecipes();
    }

    /**
     * Endpoint para buscar y filtrar recetas en el catálogo.
     * Permite combinar parámetros de búsqueda opcionales.
     *
     * @param category Categoría de la receta a buscar. (Opcional)
     * @param maxTime Tiempo máximo de preparación en minutos. (Opcional)
     * @return ResponseEntity con una lista de DTOs en formato tarjeta que cumplen los criterios.
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
package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.service.FavoriteService;
import edu.abga.foodmatch.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller to manage user favorites (matches) for recipes.
 * Provides endpoints to add, remove, and list favorite recipes for the authenticated user.
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Gestión de recetas guardadas (Matches)")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Adds a specific recipe to the authenticated user's list of favorites.
     * Represents the "Swipe Right" or "Match" action in the application.
     *
     * @param recipeId The unique identifier of the recipe to be favorited.
     * @return ResponseEntity with HTTP status 201 (Created) upon successful addition.
     */
    @PostMapping("/{recipeId}")
    @Operation(summary = "Guardar en favoritos", description = "Hace 'Match' con una receta")
    public ResponseEntity<Void> addFavorite(@PathVariable Long recipeId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        favoriteService.addFavorite(currentUserId, recipeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Removes a specific recipe from the authenticated user's list of favorites.
     *
     * @param recipeId The unique identifier of the recipe to be removed.
     * @return ResponseEntity with HTTP status 204 (No Content) upon successful removal.
     */
    @DeleteMapping("/{recipeId}")
    @Operation(summary = "Quitar de favoritos", description = "Elimina un 'Match' previo")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long recipeId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        favoriteService.removeFavorite(currentUserId, recipeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the complete list of favorite recipes for the authenticated user.
     *
     * @return ResponseEntity containing a list of {@link RecipeCardDto} representing the user's favorites,
     *         and HTTP status 200 (OK).
     */
    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Obtiene las tarjetas de recetas guardadas por el usuario")
    public ResponseEntity<List<RecipeCardDto>> getUserFavorites() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<RecipeCardDto> favorites = favoriteService.getUserFavorites(currentUserId);
        return ResponseEntity.ok(favorites);
    }
}
package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.service.FavoriteService;
import edu.abga.foodmatch.util.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Web layer of {@link FavoriteController}.
 * Verifies the HTTP endpoints related to the management of favorite recipes.
 */
@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    /**
     * Verifies the success flow when a user adds a recipe to their favorites,
     * expecting an HTTP 201 Created status.
     */
    @Test
    void addFavoriteReturnsCreatedStatus() throws Exception {
        doNothing().when(favoriteService).addFavorite(anyLong(), anyLong());

        mockMvc.perform(post("/api/favorites/1"))
                .andExpect(status().isCreated());

        verify(favoriteService).addFavorite(anyLong(), anyLong());
    }

    /**
     * Verifies the success flow when a user removes a recipe from their favorites,
     * expecting an HTTP 204 No Content status.
     */
    @Test
    void removeFavoriteReturnsNoContentStatus() throws Exception {
        doNothing().when(favoriteService).removeFavorite(anyLong(), anyLong());

        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isNoContent());

        verify(favoriteService).removeFavorite(anyLong(), anyLong());
    }

    /**
     * Verifies that the endpoint successfully retrieves the list of favorite recipes
     * for the authenticated user, returning an HTTP 200 OK and a JSON array.
     */
    @Test
    void getUserFavoritesReturnsOkAndJsonArray() throws Exception {
        when(favoriteService.getUserFavorites(anyLong())).thenReturn(List.of(UtilsForTests.recipeCardDto()));

        mockMvc.perform(get("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"));
    }
}
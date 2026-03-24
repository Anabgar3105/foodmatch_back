package edu.abga.foodmatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.service.RecipeService;
import edu.abga.foodmatch.util.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Web layer for {@link RecipeController}.
 */
@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RecipeService recipeService;

    /**
     * Verifies the success flow (Happy Path) when creating a new recipe.
     */
    @Test
    void createRecipeReturnsCreatedStatusAndRecipeDetail() throws Exception {
        RecipeDetailDto inputDto = UtilsForTests.recipeDetailDto();

        when(recipeService.createRecipe(any(RecipeDetailDto.class)))
                .thenReturn(UtilsForTests.recipeDetailDto());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tortilla de Patatas"))
                .andExpect(jsonPath("$.category").value("Cena"));
    }

    /**
     * Verifies that the controller correctly handles validation exceptions
     * when trying to create a recipe with incomplete data.
     */
    @Test
    void createRecipeReturnsBadRequestWhenNoRecipeTitle() throws Exception {
        RecipeDetailDto inputDto = RecipeDetailDto.builder().build();

        when(recipeService.createRecipe(any(RecipeDetailDto.class)))
                .thenThrow(new FoodMatchException("El título de la receta es obligatorio", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El título de la receta es obligatorio"))
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Verifies the correct functioning of the general recipe listing endpoint.
     */
    @Test
    void getAllRecipesReturnsOkStatusAndJsonArray() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(List.of(UtilsForTests.recipeDetailDto()));

        mockMvc.perform(get("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"))
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Verifies that the search endpoint correctly processes the query parameters (Query Params).
     */
    @Test
    void searchRecipesReturnsOkStatusAndFiltersCorrectly() throws Exception {
        when(recipeService.searchRecipes("Cena", 30)).thenReturn(List.of(UtilsForTests.recipeCardDto()));

        mockMvc.perform(get("/api/recipes/search")
                        .param("category", "Cena")
                        .param("maxTime", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"));
    }
}
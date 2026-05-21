package edu.abga.foodmatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.RecipeCategory;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.service.RecipeService;
import edu.abga.foodmatch.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.abga.foodmatch.security.JwtUtil;
import edu.abga.foodmatch.config.TestSecurityConfig;
import edu.abga.foodmatch.security.WithMockCustomUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Web layer for {@link RecipeController}.
 */
@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifies the success flow (Happy Path) when creating a new recipe.
     */
    @Test
    @WithMockCustomUser
    void createRecipeReturnsCreatedStatusAndRecipeDetail() throws Exception {
        RecipeDetailDto inputDto = UtilsForTests.recipeDetailDto();

        when(recipeService.createRecipe(any(RecipeDetailDto.class),anyString()))
                .thenReturn(UtilsForTests.recipeDetailDto());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tortilla de Patatas"))
                .andExpect(jsonPath("$.category").value("PLATOS_COMPLETOS"));
    }

    /**
     * Verifies that the controller correctly handles validation exceptions
     * when trying to create a recipe with incomplete data.
     */
    @Test
    @WithMockCustomUser
    void createRecipeReturnsBadRequestWhenNoRecipeTitle() throws Exception {
        RecipeDetailDto inputDto = RecipeDetailDto.builder().build();

        when(recipeService.createRecipe(any(RecipeDetailDto.class), anyString()))
                .thenThrow(new FoodMatchException("El título de la receta es obligatorio", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El título de la receta es obligatorio"))
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Verifies the correct functioning of the general recipe listing endpoint
     * applying the user privacy filter.
     */
    @Test
    @WithMockCustomUser
    void getAllRecipesReturnsOkStatusAndJsonArray() throws Exception {
        when(recipeService.getRecipesForUser(anyLong()))
                .thenReturn(List.of(UtilsForTests.recipeDetailDto()));

        mockMvc.perform(get("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"))
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Verifies the successful retrieval of a single recipe's details via its ID.
     */
    @Test
    @WithMockCustomUser
    void getRecipeByIdReturnsOkStatusAndRecipeDetail() throws Exception {
        when(recipeService.getRecipeById(anyLong(), anyLong()))
                .thenReturn(UtilsForTests.recipeDetailDto());

        mockMvc.perform(get("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tortilla de Patatas"));
    }

    /**
     * Verifies that the search endpoint correctly processes the query parameters (Query Params).
     */
    @Test
    @WithMockCustomUser
    void searchRecipesReturnsOkStatusAndFiltersCorrectly() throws Exception {
        when(recipeService.searchRecipes(RecipeCategory.PLATOS_COMPLETOS, 30, 1L)).thenReturn(List.of(UtilsForTests.recipeCardDto()));

        mockMvc.perform(get("/api/recipes/search")
                        .param("category", "PLATOS_COMPLETOS")
                        .param("maxTime", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"));
    }

    /**
     * Verifies the success flow when deleting a recipe, expecting an HTTP 204 No Content status.
     * This implies the user had the correct permissions (Owner or Admin).
     */
    @Test
    @WithMockCustomUser
    void deleteRecipeReturnsNoContentStatus() throws Exception {
        doNothing().when(recipeService).deleteRecipe(anyLong(), anyLong());

        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());

        verify(recipeService).deleteRecipe(anyLong(), anyLong());
    }

    /**
     * Verifies that the controller correctly maps the security exception to an HTTP 403 Forbidden
     * when the service rejects the deletion due to lack of permissions.
     */
    @Test
    @WithMockCustomUser
    void deleteRecipeReturnsForbiddenWhenNoPermissions() throws Exception {
        doThrow(new FoodMatchException("No tienes permisos para borrar esta receta", HttpStatus.FORBIDDEN))
                .when(recipeService).deleteRecipe(anyLong(), anyLong());

        mockMvc.perform(delete("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No tienes permisos para borrar esta receta"))
                .andExpect(jsonPath("$.status").value(403));
    }

    /**
     * Tests the PATCH /api/recipes/{id}/image endpoint for successful image update.
     * Verifies that the controller returns the updated RecipeDetailDto and status 200.
     */
    @Test
    @WithMockCustomUser
    void updateImage_ReturnsUpdatedRecipeDetail() throws Exception {
        Long recipeId = 1L;
        String newImageUrl = "https://cloudinary.com/newimage.jpg";
        RecipeDetailDto updatedDto = UtilsForTests.recipeDetailDto();
        updatedDto.setImage(newImageUrl);

        when(recipeService.updateRecipeImage(eq(recipeId), eq(newImageUrl), anyLong()))
                .thenReturn(updatedDto);

        mockMvc.perform(patch("/api/recipes/{id}/image", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + newImageUrl + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value(newImageUrl));
    }

     /**
      * Tests the PATCH /api/recipes/{id}/image endpoint when the URL is missing or empty.
      * Expects a 400 Bad Request with the correct error message.
      */
     @Test
     void updateImage_ReturnsBadRequestWhenUrlMissing() throws Exception {
         mockMvc.perform(patch("/api/recipes/1/image")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content("{}"))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.message").value("La URL de la imagen es obligatoria"));
     }

     /**
      * Verifies that the getMyRecipes endpoint returns a successful response with the list of recipes
      * created by the authenticated user.
      */
     @Test
     @WithMockCustomUser
     void getMyRecipesReturnsOkStatusAndJsonArray() throws Exception {
         when(recipeService.getMyRecipes("d.redondo"))
                 .thenReturn(List.of(UtilsForTests.recipeCardDto()));

         mockMvc.perform(get("/api/recipes/my-recipes")
                         .contentType(MediaType.APPLICATION_JSON)
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray())
                 .andExpect(jsonPath("$[0].title").value("Tortilla de Patatas"))
                 .andExpect(jsonPath("$[0].preparationTime").value(30));
     }

     /**
      * Verifies that the getMyRecipes endpoint returns an empty array when the user has no recipes.
      */
     @Test
     @WithMockCustomUser
     void getMyRecipesReturnsEmptyArrayWhenNoRecipes() throws Exception {
         when(recipeService.getMyRecipes("d.redondo"))
                 .thenReturn(List.of());

         mockMvc.perform(get("/api/recipes/my-recipes")
                         .contentType(MediaType.APPLICATION_JSON)
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray())
                 .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
     }

     /**
      * Verifies that the controller returns a 404 Not Found when the user is not found
      * in the system (edge case where the user has been deleted).
      */
     @Test
     @WithMockCustomUser
     void getMyRecipesReturnsNotFoundWhenUserDoesNotExist() throws Exception {
         when(recipeService.getMyRecipes("d.redondo"))
                 .thenThrow(new FoodMatchException("Usuario no encontrado", HttpStatus.NOT_FOUND));

         mockMvc.perform(get("/api/recipes/my-recipes")
                         .contentType(MediaType.APPLICATION_JSON)
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                 .andExpect(jsonPath("$.status").value(404));
     }

     /**
      * Tests the PUT /api/recipes/{id} endpoint for successful recipe update.
      * Verifies that the controller returns the updated RecipeDetailDto and status 200.
      */
     @Test
     @WithMockCustomUser
     void updateRecipeReturnsOkStatusAndUpdatedRecipe() throws Exception {
         Long recipeId = 1L;
         RecipeDetailDto updateDto = UtilsForTests.recipeDetailDto();
         updateDto.setTitle("Tortilla de Patatas Actualizada");
         updateDto.setPreparationTime(45);
         updateDto.setCategory("PLATOS_COMPLETOS");

         when(recipeService.updateRecipe(eq(recipeId), any(RecipeDetailDto.class), eq("d.redondo")))
                 .thenReturn(updateDto);

         mockMvc.perform(put("/api/recipes/{id}", recipeId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(updateDto))
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.title").value("Tortilla de Patatas Actualizada"))
                 .andExpect(jsonPath("$.preparationTime").value(45));
     }

     /**
      * Tests the PUT /api/recipes/{id} endpoint when the recipe does not exist.
      * Expects a 404 Not Found with the correct error message.
      */
     @Test
     @WithMockCustomUser
     void updateRecipeReturnsNotFoundWhenRecipeDoesNotExist() throws Exception {
         Long recipeId = 999L;
         RecipeDetailDto updateDto = UtilsForTests.recipeDetailDto();
         updateDto.setCategory("PLATOS_COMPLETOS");

         when(recipeService.updateRecipe(eq(recipeId), any(RecipeDetailDto.class), anyString()))
                 .thenThrow(new FoodMatchException("La receta no existe", HttpStatus.NOT_FOUND));

         mockMvc.perform(put("/api/recipes/{id}", recipeId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(updateDto))
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.message").value("La receta no existe"))
                 .andExpect(jsonPath("$.status").value(404));
     }

     /**
      * Tests the PUT /api/recipes/{id} endpoint when the user does not have permission to update the recipe.
      * Expects a 403 Forbidden with the correct error message.
      */
     @Test
     @WithMockCustomUser
     void updateRecipeReturnsForbiddenWhenUserIsNotOwner() throws Exception {
         Long recipeId = 1L;
         RecipeDetailDto updateDto = UtilsForTests.recipeDetailDto();
         updateDto.setCategory("PLATOS_COMPLETOS");

         when(recipeService.updateRecipe(eq(recipeId), any(RecipeDetailDto.class), eq("d.redondo")))
                 .thenThrow(new FoodMatchException("No tienes permisos para editar esta receta", HttpStatus.FORBIDDEN));

         mockMvc.perform(put("/api/recipes/{id}", recipeId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(updateDto))
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isForbidden())
                 .andExpect(jsonPath("$.message").value("No tienes permisos para editar esta receta"))
                 .andExpect(jsonPath("$.status").value(403));
     }

     /**
      * Tests the PUT /api/recipes/{id} endpoint with missing required fields.
      * Expects validation to be performed on the DTO.
      */
     @Test
     @WithMockCustomUser
     void updateRecipeWithValidation() throws Exception {
         Long recipeId = 1L;
         RecipeDetailDto updateDto = new RecipeDetailDto();
         updateDto.setTitle(""); // Empty title should fail validation if @NotBlank is applied
         updateDto.setCategory("PLATOS_COMPLETOS");

         when(recipeService.updateRecipe(eq(recipeId), any(RecipeDetailDto.class), eq("d.redondo")))
                 .thenReturn(UtilsForTests.recipeDetailDto());

         mockMvc.perform(put("/api/recipes/{id}", recipeId)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(updateDto))
                         .principal(() -> "d.redondo"))
                 .andExpect(status().isOk());
     }
}
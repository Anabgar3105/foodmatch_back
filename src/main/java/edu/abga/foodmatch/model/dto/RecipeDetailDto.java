package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A comprehensive Data Transfer Object for the detailed view of a recipe.
 *
 * <p>This DTO includes all information about a recipe, such as its description,
 * ingredients, and step-by-step elaboration instructions.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDetailDto {
    /**
     * The id
     */
    private Long id;

    /**
     * The title
     */
    private String title;

    /**
     * A description
     */
    private String description;

    /**
     * The  preparation time.
     */
    private Integer preparationTime;

    /**
     * The category
     */
    private String category;

    /**
     * The url of the image
     */
    private String image;

    /**
     * A list of ingredients required for the recipe.
     */
    private List<IngredientDto> ingredients;

    /**
     * A list of steps for the recipe's elaboration.
     */
    private List<ElaborationStepDto> steps;
}
package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO completo para la vista de detalle de una receta.
 * Incluye toda la información, ingredientes y pasos de elaboración.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDetailDto {
    private Long id;
    private String title;
    private String description;
    private Integer preparationTime;
    private String category;
    private String image;
    private List<IngredientDto> ingredients;
    private List<ElaborationStepDto> steps;
}
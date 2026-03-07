package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO ligero para mostrar las recetas en formato tarjeta (Swipe/Listas).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCardDto {
    private Long id;
    private String title;
    private Integer preparationTime;
    private String category;
    private String image;
}

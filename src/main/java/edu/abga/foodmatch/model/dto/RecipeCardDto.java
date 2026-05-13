package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A lightweight Data Transfer Object for displaying recipes in a card format.
 *
 * <p>This DTO is designed for lists or swipeable views, containing only the essential
 * information needed for a recipe preview card.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCardDto {
    /**
     * The id
     */
    private Long id;

    /**
     * The title
     */
    private String title;

    /**
     * The  preparation time
     */
    private Integer preparationTime;

    /**
     * The category
     */
    private String category;

    /**
     * A URL of the image
     */
    private String image;
}

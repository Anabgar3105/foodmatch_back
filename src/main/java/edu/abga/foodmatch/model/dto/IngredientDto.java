package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing a recipe ingredient.
 *
 * <p>This class is used to transfer ingredient data, including its name and quantity,
 * between the server and the client.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    /**
     * The id
     */
    private Long id;

    /**
     * The name
     */
    private String name;

    /**
     * The quantity
     */
    private String quantity;
}

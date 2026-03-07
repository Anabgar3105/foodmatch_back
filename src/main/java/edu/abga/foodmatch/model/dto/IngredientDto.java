package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar la información de un ingrediente al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    private Long id;
    private String name;
    private String quantity;
}

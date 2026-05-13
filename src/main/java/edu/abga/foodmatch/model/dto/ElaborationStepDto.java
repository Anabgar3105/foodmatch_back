package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing a single step in a recipe's elaboration process.
 *
 * <p>This class is used to transfer data related to a recipe's elaboration step
 * between the server and the client.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElaborationStepDto {
    /**
     * The is
     */
    private Long id;

    /**
     * The step number
     */
    private Integer stepNum;

    /**
     * The instruction
     */
    private String instruction;
}

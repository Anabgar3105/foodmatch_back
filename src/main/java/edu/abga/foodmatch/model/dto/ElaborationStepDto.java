package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar las instrucciones paso a paso al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElaborationStepDto {
    private Long id;
    private Integer stepNum;
    private String instruction;
}

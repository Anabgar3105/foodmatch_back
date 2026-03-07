package edu.abga.foodmatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un paso numerado en la elaboración de una receta.
 */
@Entity
@Table(name = "elaboration_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElaborationStep {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Step number
     */
    @Column(name = "step_num", nullable = false)
    private Integer stepNum;

    /**
     * Instruction
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String instruction;

    /**
     * Recipe
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
}
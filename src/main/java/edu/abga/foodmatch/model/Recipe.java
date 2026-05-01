package edu.abga.foodmatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity that represents a cooking recipe in the system.
 * Contains the dish's basic information and its relationship with the author.
 */
@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Preparation time
     */
    @Column(name = "preparation_time")
    private Integer preparationTime;

    /**
     * Category
     */
    @Column
    @Enumerated(EnumType.STRING)
    private RecipeCategory category;

    /**
     * Image
     */
    @Column(length = 255)
    private String image;

    /**
     * User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Ingredients
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients;

    /**
     * Elaboration steps
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ElaborationStep> steps;
}
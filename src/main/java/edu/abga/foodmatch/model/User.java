package edu.abga.foodmatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa a un usuario dentro del sistema FoodMatch.
 * Almacena la información de perfil, credenciales y rol de acceso.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Surname 1
     */
    @Column(nullable = false, length = 100)
    private String surname1;

    /**
     * Surname 2
     */
    @Column(length = 100)
    private String surname2;

    /**
     * Email
     */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Username
     */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * Password
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Rol
     */
    @Column(nullable = false, length = 50)
    private String role;

    /**
     * RegisterDate
     */
    @CreationTimestamp
    @Column(name = "register_date", updatable = false)
    private LocalDateTime registerDate;

    /**
     * Favourite recipes
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "favourites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> favouriteRecipes = new HashSet<>();
}

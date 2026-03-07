package edu.abga.foodmatch.repository;

import edu.abga.foodmatch.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Recipe.
 * Proporciona los métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre la tabla de recetas en la base de datos de FoodMatch.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Busca y devuelve una lista de recetas que coinciden con una categoría específica.
     *
     * @param category La categoría por la que filtrar (ej. "Desayuno", "Cena").
     * @return Lista de recetas que pertenecen a dicha categoría.
     */
    List<Recipe> findByCategory(String category);

    /**
     * Busca y devuelve una lista de recetas cuyo tiempo de preparación sea
     * menor o igual al tiempo especificado. Muy útil para filtros de "recetas rápidas".
     *
     * @param maxMinutes El tiempo máximo de preparación en minutos.
     * @return Lista de recetas que cumplen con el criterio de tiempo.
     */
    List<Recipe> findByPreparationTimeLessThanEqual(Integer maxMinutes);
}
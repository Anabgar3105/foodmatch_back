package edu.abga.foodmatch.model.mapper;

import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.Ingredient;
import edu.abga.foodmatch.model.ElaborationStep;
import edu.abga.foodmatch.model.dto.RecipeDetailDto;
import edu.abga.foodmatch.model.dto.RecipeCardDto;
import edu.abga.foodmatch.model.dto.IngredientDto;
import edu.abga.foodmatch.model.dto.ElaborationStepDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para la entidad Recipe y sus componentes.
 * Gestiona la conversión entre entidades y DTOs, incluyendo listas anidadas.
 */
@Mapper(componentModel = "spring")
public interface RecipeMapper {
    /**
     * Convierte un DTO de detalle en una entidad Recipe.
     *
     * @param dto DTO con la información detallada de la receta.
     * @return Entidad Recipe configurada para persistencia.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Recipe toEntity(RecipeDetailDto dto);

    /**
     * Convierte un DTO de ingrediente en su entidad correspondiente.
     *
     * @param dto Datos del ingrediente enviados por el cliente.
     * @return Entidad Ingredient.
     */
    Ingredient toEntity(IngredientDto dto);

    /**
     * Convierte un DTO de paso de elaboración en su entidad correspondiente.
     *
     * @param dto Datos del paso enviados por el cliente.
     * @return Entidad ElaborationStep.
     */
    ElaborationStep toEntity(ElaborationStepDto dto);

    /**
     * Transforma una entidad Recipe en un DTO de detalle.
     * Incluye de forma recursiva todos los ingredientes y pasos asociados.
     *
     * @param entity Entidad Recipe recuperada de la base de datos.
     * @return DTO con la información completa de la receta.
     */
    RecipeDetailDto toDetailDto(Recipe entity);

    /**
     * Transforma una entidad Recipe en un DTO ligero (tipo tarjeta).
     *
     * @param entity Entidad Recipe.
     * @return DTO simplificado de la receta.
     */
    RecipeCardDto toCardDto(Recipe entity);
}
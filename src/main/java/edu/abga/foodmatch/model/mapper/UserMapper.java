package edu.abga.foodmatch.model.mapper;

import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interfaz de MapStruct encargada de mapear automáticamente entre
 * los DTOs y la entidad User.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte los datos del formulario de registro en una Entidad User.
     * Asigna por defecto el rol de "USER".
     *
     * @param dto Datos enviados por el cliente para el registro.
     * @return Entidad User lista para ser persistida en la base de datos.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registerDate", ignore = true)
    @Mapping(target = "favouriteRecipes", ignore = true)
    @Mapping(target = "role", constant = "USER")
    User toEntity(UserRegistrationDto dto);

    /**
     * Convierte la Entidad User de la base de datos en un DTO de respuesta.
     *
     * @param entity La entidad User recuperada o recién guardada en la base de datos.
     * @return DTO con la información pública y segura del usuario.
     */
    UserResponseDto toResponseDto(User entity);
}

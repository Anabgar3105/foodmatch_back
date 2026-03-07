package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular los datos enviados por el cliente
 * durante el proceso de registro de un nuevo usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    private String name;
    private String surname1;
    private String surname2;
    private String email;
    private String username;
    private String password;
}
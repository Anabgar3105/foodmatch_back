package edu.abga.foodmatch.service;

import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;

/**
 * Clase de utilidades para las pruebas unitarias.
 * Centraliza la creación de objetos falsos (mocks) y DTOs necesarios para los tests.
 */
public class UtilsForTests {

    /**
     * Genera un DTO de registro simulando los datos de entrada de un nuevo usuario.
     *
     * @return UserRegistrationDto con información ficticia válida.
     */
    public static UserRegistrationDto registrationDto() {
        return UserRegistrationDto.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .password("1234")
                .build();
    }

    /**
     * Genera una entidad User simulando un registro ya persistido en la base de datos.
     *
     * @return Entidad User lista para ser devuelta por los repositorios mockeados.
     */
    public static User userEntity() {
        return User.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .password("hashedSecreta")
                .build();
    }

    /**
     * Genera un DTO de respuesta simulando la salida del sistema hacia el cliente.
     *
     * @return UserResponseDto con los datos públicos del usuario de prueba.
     */
    public static UserResponseDto userResponseDto() {
        return UserResponseDto.builder()
                .name("Dolores")
                .surname1("Redondo")
                .email("dredondo@email.com")
                .username("d.redondo")
                .build();
    }

    /**
     * Genera un DTO de login simulando un intento de autenticación del usuario.
     *
     * @return UserLoginDto con las credenciales en texto plano.
     */
    public static UserLoginDto loginDto() {
        return UserLoginDto.builder()
                .username("d.redondo")
                .password("1234")
                .build();
    }
}

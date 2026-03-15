package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de usuarios.
 * Expone los endpoints para el registro y gestión de perfiles.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios y perfiles")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para el registro de nuevos usuarios.
     *
     * @param registrationDto Objeto con la información del registro enviada en el cuerpo de la petición.
     * @return ResponseEntity con el DTO del usuario creado y el estado HTTP 201.
     */
    @PostMapping("/signup")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en la base de datos y devuelve su perfil sin la contraseña")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto registrationDto) {
        UserResponseDto createdUser = userService.registerUser(registrationDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Endpoint para el inicio de sesión de los usuarios.
     *
     * @param loginDto Objeto que contiene el nombre de usuario y la contraseña en texto plano.
     * @return ResponseEntity con los datos públicos del perfil del usuario si las credenciales son correctas.
     */
    @Operation(summary = "Login de usuario", description = "Verifica credenciales y devuelve los datos del perfil")
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }
}
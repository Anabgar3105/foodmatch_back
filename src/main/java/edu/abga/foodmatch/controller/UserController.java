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
 * REST controller for user management.
 * Exposes endpoints for registration and profile management.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios y perfiles")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint for new user registration.
     *
     * @param registrationDto Object with the registration information sent in the request body.
     * @return ResponseEntity with the DTO of the created user and HTTP status 201.
     */
    @PostMapping("/signup")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en la base de datos y devuelve su perfil sin la contraseña")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto registrationDto) {
        UserResponseDto createdUser = userService.registerUser(registrationDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Endpoint for user login.
     *
     * @param loginDto Object containing the username and password in plain text.
     * @return ResponseEntity with the public data of the user's profile if the credentials are correct.
     */
    @Operation(summary = "Login de usuario", description = "Verifica credenciales y devuelve los datos del perfil")
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }
}
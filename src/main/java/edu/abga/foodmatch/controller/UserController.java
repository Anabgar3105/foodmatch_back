package edu.abga.foodmatch.controller;

import edu.abga.foodmatch.model.dto.PasswordChangeDto;
import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

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

    /**
     * Endpoint to update the profile of the authenticated user.
     * @param principal the security principal containing the authenticated user's information
     * @param updateDto the DTO with the new profile data (username, email, avatar URL)
     * @return ResponseEntity with the updated user profile data
     */
    @Operation(summary = "Actualizar perfil de usuario", description = "Modifica username, email y/o avatar")
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            Principal principal,
            @Valid @RequestBody edu.abga.foodmatch.model.dto.UserUpdateDto updateDto) {

        UserResponseDto updatedUser = userService.updateProfile(principal.getName(), updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint to change the password of the authenticated user.
     * @param principal the security principal containing the authenticated user's information
     * @param dto the DTO with the current password and the new password
     * @return ResponseEntity with a success message if the password was updated successfully
     */
    @Operation(summary = "Actualizar contraseña", description = "Permite al usuario autenticado cambiar su contraseña actual")
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            Principal principal,
            @Valid @RequestBody PasswordChangeDto dto) {

        userService.changePassword(principal.getName(), dto);

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Contraseña actualizada con éxito");

        return ResponseEntity.ok(response);
    }
}
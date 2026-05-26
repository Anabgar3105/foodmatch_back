package edu.abga.foodmatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.abga.foodmatch.UtilsForTests;
import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.*;
import edu.abga.foodmatch.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.abga.foodmatch.security.JwtUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Web layer for {@link UserController}.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    /**
     * Verifies the success flow (Happy Path) of user registration.
     */
    @Test
    void registerSuccessReturnsCreatedStatusAndUserDto() throws Exception {
        UserRegistrationDto inputDto = UtilsForTests.registrationDto();

        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(UtilsForTests.userResponseDto());

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("dredondo@email.com"))
                .andExpect(jsonPath("$.username").value("d.redondo"));
    }

    /**
     * Verifies that the controller correctly intercepts validation errors.
     */
    @Test
    void registerReturnsBadRequestWhenDataIsInvalid() throws Exception {
        UserRegistrationDto inputDto = UserRegistrationDto.builder()
                .username("nocuela")
                .build();

        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Los datos proporcionados no son válidos"))
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Verifies the success flow for user authentication.
     */
    @Test
    void loginReturnsOkStatusAndUserDto() throws Exception {
        UserLoginDto loginDto = UtilsForTests.loginDto();

        when(userService.login(any(UserLoginDto.class))).thenReturn(UtilsForTests.userResponseDto());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("dredondo@email.com"));
    }

    /**
     * Verifies the system's behavior in the event of an unauthorized access attempt
     * with an incorrect password.
     */
    @Test
    void loginReturnsUnauthorizedWhenCredentialsAreWrong() throws Exception {
        UserLoginDto loginDto = UserLoginDto.builder()
                .username("hacker")
                .password("contraseña_incorrecta")
                .build();

        when(userService.login(any(UserLoginDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    /**
     * Verifies the system's behavior in the event of an access attempt by a user
     * that does not exist in the database records.
     */
    @Test
    void loginReturnsNotFoundWhenCredentialsAreWrong() throws Exception {
        UserLoginDto loginDto = UserLoginDto.builder()
                .username("sr_invisible")
                .password("no_existo")
                .build();

        when(userService.login(any(UserLoginDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    /**
     * Verifies the success flow when updating a user profile with valid data.
     */
    @Test
    void updateProfile_SuccessReturnsOkStatusAndUpdatedUserDto() throws Exception {
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo.updated", "updated@email.com", "https://avatar.url");

        when(userService.updateProfile(anyString(), any(UserUpdateDto.class)))
                .thenReturn(UtilsForTests.userResponseDto());

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("dredondo@email.com"))
                .andExpect(jsonPath("$.username").value("d.redondo"));
    }

    /**
     * Verifies that the controller returns a NOT_FOUND error when attempting to update
     * a profile for a user that does not exist in the database.
     */
    @Test
    void updateProfile_ReturnsNotFoundWhenUserDoesNotExist() throws Exception {
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("nonexistent", "nonexistent@email.com", "https://avatar.url");

        when(userService.updateProfile(anyString(), any(UserUpdateDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(() -> "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    /**
     * Verifies that the controller returns a CONFLICT error when attempting to update
     * the profile with a username that is already in use by another user.
     */
    @Test
    void updateProfile_ReturnsConflictWhenUsernameAlreadyInUse() throws Exception {
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("existing.user", "updated@email.com", "https://avatar.url");

        when(userService.updateProfile(anyString(), any(UserUpdateDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.DUPLICATE_USERNAME, HttpStatus.CONFLICT));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este nombre de usuario ya existe"))
                .andExpect(jsonPath("$.status").value(409));
    }

    /**
     * Verifies that the controller returns a CONFLICT error when attempting to update
     * the profile with an email that is already in use by another user.
     */
    @Test
    void updateProfile_ReturnsConflictWhenEmailAlreadyInUse() throws Exception {
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo.updated", "existing@email.com", "https://avatar.url");

        when(userService.updateProfile(anyString(), any(UserUpdateDto.class)))
                .thenThrow(new FoodMatchException(ErrorCode.DUPLICATE_EMAIL, HttpStatus.CONFLICT));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este email ya está registrado"));
    }

    /**
     * Verifies that the controller correctly updates the password when the
     * current password is correct and the new password meets the requirements.
     */
    @Test
    void changePassword_SuccessReturnsOkAndSuccessMessage() throws Exception {
        PasswordChangeDto passwordDto = UtilsForTests.passwordChangeDto("Secreta123", "NewPassword456");

        doNothing().when(userService).changePassword(anyString(), any(PasswordChangeDto.class));

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña actualizada con éxito"));
    }

    /**
     * Verifies that the controller returns a BAD REQUEST error when the current password
     * provided does not match the actual password of the authenticated user.
     */
    @Test
    void changePassword_ReturnsBadRequestWhenCurrentPasswordIsIncorrect() throws Exception {
        PasswordChangeDto passwordDto = UtilsForTests.passwordChangeDto("WrongPassword", "NewPassword456");

        doThrow(new FoodMatchException(ErrorCode.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST))
                .when(userService).changePassword(anyString(), any(PasswordChangeDto.class));

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .principal(() -> "d.redondo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"))
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Verifies that the controller returns a NOT FOUND when attempting to change
     * the password for a user that does not exist in the database.
     */
    @Test
    void changePassword_ReturnsNotFoundWhenUserDoesNotExist() throws Exception {
        PasswordChangeDto passwordDto = UtilsForTests.passwordChangeDto("Secreta123", "NewPassword456");

        doThrow(new FoodMatchException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND))
                .when(userService).changePassword(anyString(), any(PasswordChangeDto.class));

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .principal(() -> "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }
}
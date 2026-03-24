package edu.abga.foodmatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.service.UserService;
import edu.abga.foodmatch.util.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .thenThrow(new FoodMatchException("Faltan datos obligatorios", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Faltan datos obligatorios"))
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
                .thenThrow(new FoodMatchException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
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
                .thenThrow(new FoodMatchException("El usuario no existe", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }
}
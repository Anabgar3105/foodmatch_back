package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
import edu.abga.foodmatch.util.UtilsForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link UserService} class.
 * All mock data is provided by the {@link UtilsForTests} helper class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /**
     * Verifies that the registration flow is completed successfully when
     * the data is correct and the user does not previously exist in the system.
     */
    @Test
    void registerUserSuccess() {

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserRegistrationDto.class))).thenReturn(UtilsForTests.userEntity());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(UtilsForTests.userEntity());
        when(userMapper.toResponseDto(any(User.class))).thenReturn(UtilsForTests.userResponseDto());

        UserResponseDto result = userService.registerUser(UtilsForTests.registrationDto());

        assertNotNull(result);
        assertEquals("Dolores", result.getName());
        assertEquals("Redondo", result.getSurname1());
        assertEquals("dredondo@email.com", result.getEmail());
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).existsByUsername(anyString());
        verify(userMapper).toEntity(any(UserRegistrationDto.class));
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponseDto(any(User.class));
    }

    /**
     * Verifies that the system rejects the registration and throws a {@link FoodMatchException}
     * with HTTP code 409 when the provided email is already registered.
     */
    @Test
    void registerUserThrowsExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("dredondo@email.com")).thenReturn(true);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.registerUser(UtilsForTests.registrationDto()));

        assertEquals("El email ya está en uso", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that the system rejects the registration and throws a {@link FoodMatchException}
     * with code 409 when the username is already in use.
     */
    @Test
    void registerUserThrowsExceptionWhenUsernameAlreadyExists() {

        when(userRepository.existsByUsername("d.redondo")).thenReturn(true);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.registerUser(UtilsForTests.registrationDto()));

        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that the login process works correctly when
     * the user exists in the database and the plain password matches
     * the stored hash.
     */
    @Test
    void loginSuccess() {

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(UtilsForTests.userEntity()));
        when(passwordEncoder.matches("1234", "hashedSecreta")).thenReturn(true);
        when(userMapper.toResponseDto(UtilsForTests.userEntity())).thenReturn(UtilsForTests.userResponseDto());

        UserResponseDto result = userService.login(UtilsForTests.loginDto());

        assertNotNull(result);
        assertEquals("d.redondo", result.getUsername());
    }

    /**
     * Verifies that the login fails and throws a {@link FoodMatchException}
     * with code 401 when the user exists, but
     * the entered password is incorrect.
     */
    @Test
    void loginThrowsExceptionWhenPasswordIsIncorrect() {
        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(UtilsForTests.userEntity()));
        when(passwordEncoder.matches("1234", "hashedSecreta")).thenReturn(false);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.login(UtilsForTests.loginDto()));

        assertEquals("Credenciales incorrectas", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}
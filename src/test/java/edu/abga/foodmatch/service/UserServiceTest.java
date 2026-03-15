package edu.abga.foodmatch.service;

import edu.abga.foodmatch.handler.FoodMatchException;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
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
 * Suite de pruebas unitarias para la clase {@link UserService}.
 * Todos los datos ficticios se proveen mediante la clase auxiliar {@link UtilsForTests}.
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
     * Verifica que el flujo de registro se completa satisfactoriamente cuando
     * los datos son correctos y el usuario no existe previamente en el sistema.
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
     * Verifica que el sistema rechaza el registro y lanza un {@link FoodMatchException}
     * con estado HTTP 409 (Conflict) cuando el email proporcionado ya está registrado.
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
     * Verifica que el sistema rechaza el registro y lanza un {@link FoodMatchException}
     * con estado HTTP 409 (Conflict) cuando el nombre de usuario (username) ya está en uso.
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
     * Verifica que el proceso de inicio de sesión funciona correctamente cuando
     * el usuario existe en la base de datos y la contraseña plana coincide con
     * el hash almacenado.
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
     * Verifica que el inicio de sesión falla y lanza un {@link FoodMatchException}
     * con estado HTTP 401 (Unauthorized) cuando el usuario existe, pero
     * la contraseña introducida es incorrecta.
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
package edu.abga.foodmatch.service;

import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.*;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
import edu.abga.foodmatch.security.JwtUtil;
import edu.abga.foodmatch.UtilsForTests;
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

    @Mock
    private JwtUtil jwtUtil;

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
        when(jwtUtil.generateToken(anyString())).thenReturn("token");

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

        assertEquals("El usuario o la contraseña son incorrectos", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    /**
     * Verifies that the profile update process completes successfully when
     * the provided data is valid and unique (username and email do not already exist).
     */
    @Test
    void updateProfileSuccess() {
        User user = UtilsForTests.userEntity();
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo.updated", "updated@email.com", "https://avatar.url");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("d.redondo.updated")).thenReturn(false);
        when(userRepository.existsByEmail("updated@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(UtilsForTests.userResponseDto());
        when(jwtUtil.generateToken(anyString())).thenReturn("new_token");

        UserResponseDto result = userService.updateProfile("d.redondo", updateDto);

        assertNotNull(result);
        assertEquals("d.redondo", result.getUsername());
        assertEquals("dredondo@email.com", result.getEmail());
        verify(userRepository).findByUsername("d.redondo");
        verify(userRepository).existsByUsername("d.redondo.updated");
        verify(userRepository).existsByEmail("updated@email.com");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(anyString());
    }

    /**
     * Verifies that the profile update fails and throws a {@link FoodMatchException}
     * with code 404 when the user attempting to update does not exist in the database.
     */
    @Test
    void updateProfileThrowsExceptionWhenUserDoesNotExist() {
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo.updated", "updated@email.com", "https://avatar.url");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.updateProfile("nonexistent", updateDto));

        assertEquals("El usuario no existe", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that the profile update fails and throws a {@link FoodMatchException}
     * with code 409 when the new username is already in use by another user.
     */
    @Test
    void updateProfileThrowsExceptionWhenUsernameAlreadyInUse() {
        User user = UtilsForTests.userEntity();
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("existing.user", "updated@email.com", "https://avatar.url");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existing.user")).thenReturn(true);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.updateProfile("d.redondo", updateDto));

        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that the profile update fails and throws a {@link FoodMatchException}
     * with code 409 when the new email is already in use by another user.
     */
    @Test
    void updateProfileThrowsExceptionWhenEmailAlreadyInUse() {
        User user = UtilsForTests.userEntity();
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo.updated", "existing@email.com", "https://avatar.url");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("d.redondo.updated")).thenReturn(false);
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.updateProfile("d.redondo", updateDto));

        assertEquals("El email ya está en uso", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that when a user updates their profile with the same username and email,
     * the system does not perform duplicate checks and proceeds with the update successfully.
     */
    @Test
    void updateProfileSuccessWhenUsernameAndEmailRemainTheSame() {
        User user = UtilsForTests.userEntity();
        UserUpdateDto updateDto = UtilsForTests.userUpdateDto("d.redondo", "dredondo@email.com", "https://new.avatar.url");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(UtilsForTests.userResponseDto());
        when(jwtUtil.generateToken(anyString())).thenReturn("new_token");

        UserResponseDto result = userService.updateProfile("d.redondo", updateDto);

        assertNotNull(result);
        verify(userRepository).findByUsername("d.redondo");
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Verifies that the password change process completes successfully when
     * the current password matches and the new password meets the requirements.
     */
    @Test
    void changePasswordSuccess() {
        User user = UtilsForTests.userEntity();
        PasswordChangeDto passwordChangeDto = UtilsForTests.passwordChangeDto("Secreta123", "NewPassword456");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Secreta123", "hashedSecreta")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword456")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.changePassword("d.redondo", passwordChangeDto));

        verify(userRepository).findByUsername("d.redondo");
        verify(passwordEncoder).matches("Secreta123", "hashedSecreta");
        verify(passwordEncoder).encode("NewPassword456");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Verifies that the password change fails and throws a {@link FoodMatchException}
     * with code 404 when the user attempting to change their password does not exist.
     */
    @Test
    void changePasswordThrowsExceptionWhenUserDoesNotExist() {
        PasswordChangeDto passwordChangeDto = UtilsForTests.passwordChangeDto("Secreta123", "NewPassword456");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.changePassword("nonexistent", passwordChangeDto));

        assertEquals("Usuario no encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Verifies that the password change fails and throws a {@link FoodMatchException}
     * with code 400 when the current password provided does not match the actual password
     * stored in the database.
     */
    @Test
    void changePasswordThrowsExceptionWhenCurrentPasswordIsIncorrect() {
        User user = UtilsForTests.userEntity();
        PasswordChangeDto passwordChangeDto = UtilsForTests.passwordChangeDto("WrongPassword", "NewPassword456");

        when(userRepository.findByUsername("d.redondo")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedSecreta")).thenReturn(false);

        FoodMatchException exception = assertThrows(FoodMatchException.class, () -> userService.changePassword("d.redondo", passwordChangeDto));

        assertEquals("La contraseña actual es incorrecta", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }
}
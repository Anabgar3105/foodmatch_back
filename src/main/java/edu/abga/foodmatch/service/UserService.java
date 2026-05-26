package edu.abga.foodmatch.service;


import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Role;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.*;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
import edu.abga.foodmatch.security.JwtUtil;
import edu.abga.foodmatch.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service that contains the business logic for user management.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Processes the registration of a new user in the system.
     * Validates that the email and username do not already exist.
     * Transforms the input DTO to an Entity, saves it, and returns a secure response.
     *
     * @param registrationDto Object with the user's registration data.
     * @return UserResponseDto with the data of the newly created user.
     * @throws FoodMatchException If the registration data is not valid.
     */
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        ValidationUtils.validateRegistrationData(registrationDto);

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new FoodMatchException(ErrorCode.DUPLICATE_EMAIL, "Este email ya está registrado", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new FoodMatchException(ErrorCode.DUPLICATE_USERNAME, "Este nombre de usuario ya existe", HttpStatus.CONFLICT);
        }

        User userToSave = userMapper.toEntity(registrationDto);
        userToSave.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        userToSave.setRole(Role.USER);
        User savedUser = userRepository.save(userToSave);

        return userMapper.toResponseDto(savedUser);
    }


    /**
     * Verifies a user's credentials.
     *
     * @return The user's DTO if the data is correct.
     * @throws FoodMatchException if the credentials do not match.
     */
    public UserResponseDto login(UserLoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new FoodMatchException(ErrorCode.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new FoodMatchException(ErrorCode.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        }

        UserResponseDto response = userMapper.toResponseDto(user);

        String token = jwtUtil.generateToken(user.getUsername());
        response.setToken(token);

        return response;
    }

    /**
     * Updates the profile of an existing user.
     * @param currentUsername The username of the user to update.
     * @param updateDto Object with the new profile data.
     * @return UserResponseDto with the updated user data.
     * @throws FoodMatchException if the current password is incorrect or if the user does not exist.
     */
    public UserResponseDto updateProfile(String currentUsername, UserUpdateDto updateDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, "El usuario no existe", HttpStatus.NOT_FOUND));

        if (!user.getUsername().equals(updateDto.getUsername()) &&
                userRepository.existsByUsername(updateDto.getUsername())) {
            throw new FoodMatchException(ErrorCode.DUPLICATE_USERNAME, "Este nombre de usuario ya existe", HttpStatus.CONFLICT);
        }

        if (!user.getEmail().equals(updateDto.getEmail()) &&
                userRepository.existsByEmail(updateDto.getEmail())) {
            throw new FoodMatchException(ErrorCode.DUPLICATE_EMAIL, "Este email ya está registrado", HttpStatus.CONFLICT);
        }

        user.setUsername(updateDto.getUsername());
        user.setEmail(updateDto.getEmail());
        user.setAvatarUrl(updateDto.getAvatarUrl());

        User savedUser = userRepository.save(user);

        UserResponseDto response = userMapper.toResponseDto(savedUser);


        String newToken = jwtUtil.generateToken(savedUser.getUsername());
        response.setToken(newToken);

        return response;
    }


    /**
     * Changes the password of an existing user.
     * @param currentUsername the username of the user to update.
     * @param dto the DTO with the current and new password.
     * @throws FoodMatchException if the current password is incorrect or if the user does not exist.
     */
    public void changePassword(String currentUsername, PasswordChangeDto dto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new FoodMatchException(ErrorCode.USER_NOT_FOUND, "Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new FoodMatchException(ErrorCode.INVALID_CREDENTIALS, "La contraseña actual es incorrecta", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
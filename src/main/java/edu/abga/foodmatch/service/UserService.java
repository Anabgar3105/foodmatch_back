package edu.abga.foodmatch.service;


import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.model.Role;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
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
            throw new FoodMatchException("El email ya está en uso", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new FoodMatchException("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);
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
                .orElseThrow(() -> new FoodMatchException("El usuario no existe", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new FoodMatchException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        return userMapper.toResponseDto(user);
    }
}
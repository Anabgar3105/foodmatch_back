package edu.abga.foodmatch.service;


import edu.abga.foodmatch.handler.FoodMatchException;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserLoginDto;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Procesa el registro de un nuevo usuario en el sistema.
     * Valida que el email y el nombre de usuario no existan previamente.
     * Transforma el DTO de entrada a Entidad, lo guarda y devuelve una respuesta segura.
     *
     * @param registrationDto Objeto con los datos de registro del usuario.
     * @return UserResponseDto con los datos del usuario recién creado.
     * @throws FoodMatchException Si el email o el nombre de usuario ya están en uso en la BD.
     */
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new FoodMatchException("El email ya está en uso", HttpStatus.CONFLICT);        }
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new FoodMatchException("El nombre de usuario ya está en uso", HttpStatus.CONFLICT);        }

        User userToSave = userMapper.toEntity(registrationDto);
        userToSave.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        User savedUser = userRepository.save(userToSave);

        return userMapper.toResponseDto(savedUser);
    }


    /**
     * Verifica las credenciales de un usuario.
     * @return El DTO del usuario si los datos son correctos.
     * @throws FoodMatchException si las credenciales no coinciden.
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
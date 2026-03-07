package edu.abga.foodmatch.service;


import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.model.dto.UserRegistrationDto;
import edu.abga.foodmatch.model.dto.UserResponseDto;
import edu.abga.foodmatch.model.mapper.UserMapper;
import edu.abga.foodmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Procesa el registro de un nuevo usuario en el sistema.
     * Valida que el email y el nombre de usuario no existan previamente.
     * Transforma el DTO de entrada a Entidad, lo guarda y devuelve una respuesta segura.
     *
     * @param registrationDto Objeto con los datos de registro del usuario.
     * @return UserResponseDto con los datos del usuario recién creado.
     * @throws RuntimeException Si el email o el nombre de usuario ya están en uso en la BD.
     */
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso");
        }
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso");
        }

        User userToSave = userMapper.toEntity(registrationDto);
        User savedUser = userRepository.save(userToSave);

        return userMapper.toResponseDto(savedUser);
    }
}
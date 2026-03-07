package edu.abga.foodmatch.repository;

import edu.abga.foodmatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Gestiona el acceso a los datos de los usuarios, siendo fundamental para
 * los procesos de autenticación y registro (Login/Sign Up).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario en la base de datos mediante su dirección de correo electrónico.
     *
     * @param email El correo electrónico exacto del usuario.
     * @return Un objeto Optional que contiene el usuario si se encuentra, o vacío si no existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario en la base de datos mediante su nombre de usuario.
     *
     * @param username El nombre de usuario (username) único.
     * @return Un objeto Optional que contiene el usuario si se encuentra, o vacío si no existe.
     */
    Optional<User> findByUsername(String username);

    /**
     * Comprueba si ya existe un usuario registrado en el sistema con un email determinado.
     *
     * @param email El correo electrónico a verificar.
     * @return true si el email ya está en uso, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Comprueba si ya existe un usuario registrado en el sistema con un username determinado.
     *
     * @param username El nombre de usuario a verificar.
     * @return true si el username ya está en uso, false en caso contrario.
     */
    boolean existsByUsername(String username);
}
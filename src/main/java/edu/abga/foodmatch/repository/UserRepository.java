package edu.abga.foodmatch.repository;

import edu.abga.foodmatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the User entity.
 * Manages data access for users and is key to authentication
 * and registration processes (Login/Sign Up).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     *
     * @param email Exact user email address.
     * @return An Optional containing the user if found, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by username.
     *
     * @param username Unique username.
     * @return An Optional containing the user if found, or empty if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email Email address to verify.
     * @return true if the email is already in use, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username Username to verify.
     * @return true if the username is already in use, false otherwise.
     */
    boolean existsByUsername(String username);
}
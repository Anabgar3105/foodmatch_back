package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for encapsulating user registration data.
 *
 * <p>This class is used to transfer the information provided by a new user
 * during the registration process from the client to the server.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
    /**
     * The name
     */
    private String name;

    /**
     * The first surname
     */
    private String surname1;

    /**
     * The second surname
     */
    private String surname2;

    /**
     * The email
     */
    private String email;

    /**
     * The username
     */
    private String username;

    /**
     * The password
     */
    private String password;
}
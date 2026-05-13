package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login credentials.
 *
 * <p>This class encapsulates the username and password provided by a user
 * during the authentication process.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
    /**
     * The username.
     */
    private String username;

    /**
     * The password.
     */
    private String password;
}

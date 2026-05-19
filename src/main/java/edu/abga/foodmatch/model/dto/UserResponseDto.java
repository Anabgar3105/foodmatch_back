package edu.abga.foodmatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for sending user information back to the client.
 *
 * <p>This DTO contains public user data, typically sent after a successful
 * login or registration. It includes an authentication token.</p>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    /**
     * The id
     */
    private Long id;

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
     * The username.
     */
    private String username;

    /**
     * The date and time when the user registered.
     */
    private LocalDateTime registerDate;

    /**
     * The authentication token
     */
    private String token;

    /**
     * The avatar URL
     */
    private String avatarUrl;
}

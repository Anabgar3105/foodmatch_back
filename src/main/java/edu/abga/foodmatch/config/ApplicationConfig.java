package edu.abga.foodmatch.config;

import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Application-level security bean configuration.
 *
 * <p>This class provides core beans required by the authentication flow,
 * including user lookup, password encoding, and authentication manager wiring.</p>
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Creates a {@link UserDetailsService} implementation that loads users by username
     * from the persistence layer.
     *
     * @return a {@link UserDetailsService} used by Spring Security during authentication
     * @throws UsernameNotFoundException if no user is found for the provided username
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User myUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .roles(myUser.getRole().name())
                    .build();
        };
    }

    /**
     * Creates the password encoder used to hash and verify user passwords.
     *
     * @return a BCrypt-based password encoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the Spring Security {@link AuthenticationManager} from the provided
     * {@link AuthenticationConfiguration}.
     *
     * @param config the authentication configuration
     * @return the configured authentication manager
     * @throws Exception if the authentication manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
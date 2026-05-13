package edu.abga.foodmatch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test configuration for Spring Security.
 * Permits all requests to allow @WithMockCustomUser annotation to work properly
 * while avoiding authentication issues in unit tests.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Configures a test security filter chain that allows all requests.
     * This enables @WithMockCustomUser to inject mock users into the security context
     * without requiring JWT tokens.
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.disable());

        return http.build();
    }
}



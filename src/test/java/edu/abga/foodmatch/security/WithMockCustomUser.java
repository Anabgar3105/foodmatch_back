package edu.abga.foodmatch.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation to provide a mock CustomUserDetails with a valid ID for testing.
 * This annotation is used instead of @WithMockUser because the application
 * expects CustomUserDetails instances with an ID field.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    /**
     * The ID of the mock user.
     */
    long id() default 1L;

    /**
     * The username of the mock user.
     */
    String username() default "user";

    /**
     * The roles of the mock user.
     */
    String[] roles() default {"ROLE_USER"};
}


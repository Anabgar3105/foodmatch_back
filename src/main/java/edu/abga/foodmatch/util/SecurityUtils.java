package edu.abga.foodmatch.util;

/**
 * Utility class to extract information from the authenticated user.
 */
public class SecurityUtils {

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * TODO: Replace this mock implementation with real Spring Security JWT extraction.
     * Example future implementation:
     * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
     * return ((CustomUserDetails) auth.getPrincipal()).getId();
     *
     * @return The user ID.
     */
    public static Long getCurrentUserId() {
        // MOCK TEMPORAL: Siempre devuelve el usuario con ID 1
        return 1L;
    }
}
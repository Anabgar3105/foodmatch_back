package edu.abga.foodmatch.util;

import edu.abga.foodmatch.exception.ErrorCode;
import edu.abga.foodmatch.exception.FoodMatchException;
import edu.abga.foodmatch.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Utility class to extract information from the authenticated user.
 */
public class SecurityUtils {

    /**
     * Retrieves the ID of the currently authenticated user based on the JWT token.
     * @return The user ID.
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || Objects.requireNonNull(authentication.getPrincipal()).equals("anonymousUser")) {
            throw new FoodMatchException(ErrorCode.TOKEN_INVALID, "No hay un usuario autenticado en la sesión", HttpStatus.UNAUTHORIZED);
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getId();
        }

        throw new FoodMatchException(ErrorCode.INTERNAL_SERVER_ERROR, "Error al extraer la información del usuario de seguridad", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
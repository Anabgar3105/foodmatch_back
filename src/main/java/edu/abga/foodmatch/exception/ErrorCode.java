package edu.abga.foodmatch.exception;

/**
 * Standardized error codes for the FoodMatch application.
 * <p>This enum defines a set of error codes that can be used throughout the application to represent specific error conditions in a consistent way.
 * Each error code has a unique identifier and a user-friendly message that can be returned in API responses to provide clear feedback to clients about what went wrong.</p>
 */
public enum ErrorCode {
    // Errores de validación (400)
    INVALID_INPUT("INVALID_INPUT", "Los datos proporcionados no son válidos"),
    MISSING_FIELD("MISSING_FIELD", "Falta un campo requerido"),
    INVALID_EMAIL("INVALID_EMAIL", "El formato del email no es válido"),
    WEAK_PASSWORD("WEAK_PASSWORD", "La contraseña no es segura"),
    
    // Errores de autenticación (401)
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Usuario o contraseña incorrectos"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Tu sesión ha expirado"),
    TOKEN_INVALID("TOKEN_INVALID", "Token de autenticación inválido"),
    
    // Errores de autorización (403)
    INSUFFICIENT_PERMISSIONS("INSUFFICIENT_PERMISSIONS", "No tienes permisos para realizar esta acción"),
    
    // Errores de recursos (404)
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "El recurso solicitado no existe"),
    USER_NOT_FOUND("USER_NOT_FOUND", "El usuario no existe"),
    RECIPE_NOT_FOUND("RECIPE_NOT_FOUND", "La receta no existe"),
    
    // Errores de conflicto (409)
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "Este email ya está registrado"),
    DUPLICATE_USERNAME("DUPLICATE_USERNAME", "Este nombre de usuario ya existe"),
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS", "El recurso ya existe"),
    
    // Errores de imagen/archivo (400)
    IMAGE_UPLOAD_FAILED("IMAGE_UPLOAD_FAILED", "Error al subir la imagen"),
    INVALID_IMAGE_FORMAT("INVALID_IMAGE_FORMAT", "El formato de la imagen no es válido"),
    IMAGE_TOO_LARGE("IMAGE_TOO_LARGE", "La imagen es demasiado grande"),
    
    // Errores del servidor (500)
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Algo salió mal en nuestros servidores"),
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "Error al conectar con un servicio externo");

    private final String code;
    private final String userMessage;

    ErrorCode(String code, String userMessage) {
        this.code = code;
        this.userMessage = userMessage;
    }

    public String getCode() {
        return code;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

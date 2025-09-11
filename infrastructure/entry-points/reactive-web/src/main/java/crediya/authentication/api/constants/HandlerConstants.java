package crediya.authentication.api.constants;

public final class HandlerConstants {
    
    // HTTP Headers
    public static final String USER_AGENT_HEADER = "User-Agent";
    
    // Default values
    public static final String UNKNOWN_ADDRESS = "unknown";
    
    // Validation binding result names
    public static final String USER_CREATE_REQUEST_BINDING_NAME = "userCreateRequest";
    public static final String LOGIN_REQUEST_BINDING_NAME = "loginRequest";
    
    // Token configuration
    public static final String BEARER_TOKEN_TYPE = "Bearer";
    public static final Long DEFAULT_TOKEN_EXPIRATION_SECONDS = 86400L; // 24 hours

    public static final String USER_AUTHENTICATED_SUCCESS = "User authenticated successfully";

    // Error messages for authorization
    public static final String INSUFFICIENT_PERMISSIONS_CREATE_USERS = "Insufficient permissions to create users";
    public static final String INSUFFICIENT_PERMISSIONS_VIEW_USERS = "Insufficient permissions to view all users";
    
    // Validation messages
    public static final String VALIDATION_FAILED_PREFIX = "Validation failed: ";
    public static final String VALIDATION_ERROR_SEPARATOR = "; ";
    
    // Log messages specific to Handler
    public static final String LOGIN_REQUEST_RECEIVED_LOG = "Login request received from: {}";
    public static final String LOGIN_VALIDATION_FAILED_LOG = "Login validation failed: {}";
    public static final String AUTHENTICATION_FAILED_LOG = "Authentication failed: {}";
    
    // JSON error response templates
    public static final String ERROR_JSON_TEMPLATE = "{\"error\":\"%s\"}";
    
    private HandlerConstants() {
        // Utility class - prevent instantiation
    }
}
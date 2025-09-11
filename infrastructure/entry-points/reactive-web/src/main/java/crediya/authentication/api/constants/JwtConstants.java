package crediya.authentication.api.constants;

public final class JwtConstants {
    
    // JWT Claims
    public static final String ROLE_ID_CLAIM = "roleId";
    
    // HTTP Authentication
    public static final String BEARER_PREFIX = "Bearer ";
    
    // Context Attributes (reused from SecurityContextExtractor but centralized here)
    public static final String USER_ID_ATTRIBUTE = "userId";
    public static final String ROLE_ID_ATTRIBUTE = "roleId";
    
    // Error response fields
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String STATUS_FIELD = "status";
    public static final String ERROR_FIELD = "error";
    public static final String MESSAGE_FIELD = "message";
    
    // Error messages
    public static final String UNAUTHORIZED_ERROR = "Unauthorized";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied. Valid JWT token required.";
    
    // HTTP Status codes as constants
    public static final int UNAUTHORIZED_STATUS_CODE = 401;
    
    // JSON response template for unauthorized access
    public static final String UNAUTHORIZED_JSON_TEMPLATE = 
        "{\"%s\":\"%s\",\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\"}";
    
    private JwtConstants() {
        // Utility class - prevent instantiation
    }
}
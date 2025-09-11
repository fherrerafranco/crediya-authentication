package crediya.authentication.api.constants;

/**
 * Constants for error messages used in exception handling.
 * Centralizes all error messages to ensure consistency across the application.
 */
public final class ErrorMessages {
    
    // HTTP Status Messages
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String VALIDATION_FAILED = "Validation Failed";
    public static final String RESOURCE_NOT_FOUND = "The requested resource was not found";
    public static final String CONFLICT = "Conflict";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String UNAUTHORIZED = "Unauthorized";
    
    // Validation Messages (specific field validation messages are defined below)
    
    // Field validation messages
    public static final String FIRST_NAME_REQUIRED = "First name is required";
    public static final String LAST_NAME_REQUIRED = "Last name is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_MUST_BE_VALID = "Email must be valid";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String BASE_SALARY_REQUIRED = "Base salary is required";
    
    // Database Constraint Messages
    public static final String NOT_NULL_CONSTRAINT = "not-null constraint";
    public static final String DUPLICATE_KEY_VALUE = "duplicate key value";
    public static final String USER_ID_CONSTRAINT = "user_id";
    public static final String EMAIL_CONSTRAINT = "email";
    
    // Authentication Messages
    public static final String LOGIN_REQUEST_RECEIVED = "Login request received from: {}";
    public static final String AUTHENTICATION_FAILED = "Authentication failed: {}";
    public static final String LOGIN_VALIDATION_FAILED = "Login validation failed: {}";
    public static final String VALIDATION_FAILED_PREFIX = "Validation failed: ";
    
    private ErrorMessages() {
        // Utility class - prevent instantiation
    }
}
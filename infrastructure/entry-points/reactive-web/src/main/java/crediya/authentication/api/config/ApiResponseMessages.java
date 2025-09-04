package crediya.authentication.api.config;

/**
 * Constants for API response messages used in REST endpoints.
 * Centralizes all user-facing response messages to ensure consistency.
 */
public final class ApiResponseMessages {
    
    // Success messages
    public static final String USER_CREATED_SUCCESS = "User created successfully";
    public static final String AUTH_SUCCESS = "Authentication successful";
    public static final String USERS_RETRIEVED_SUCCESS = "Users retrieved successfully";
    
    // Error messages
    public static final String AUTH_FAILED = "Authentication failed";
    public static final String INVALID_INPUT_DATA = "Invalid input data";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    
    // API operation descriptions
    public static final String USER_AUTH_DESCRIPTION = "Authenticates a user with email and password, returns JWT token";
    public static final String USER_AUTH_SUMMARY = "User authentication";
    public static final String CREATE_USER_DESCRIPTION = "Creates a new user in the system";
    public static final String CREATE_USER_SUMMARY = "Create a new user";
    public static final String GET_USERS_DESCRIPTION = "Retrieves all users from the system";
    public static final String GET_USERS_SUMMARY = "Get all users";
    
    // Request/Response descriptions
    public static final String LOGIN_CREDENTIALS_DESCRIPTION = "Login credentials";
    public static final String USER_DATA_DESCRIPTION = "User data";
    
    private ApiResponseMessages() {
        // Utility class - prevent instantiation
    }
}
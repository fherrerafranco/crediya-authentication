package crediya.authentication.api.constants;

public final class LogMessages {
    
    private LogMessages() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Request logging templates
    public static final String POST_REQUEST_RECEIVED = "Received POST request to create user from IP: {}, User-Agent: {}";
    public static final String GET_REQUEST_RECEIVED = "Received GET request to retrieve all users from IP: {}, User-Agent: {}";
    
    // Processing logging templates
    public static final String REQUEST_PARSED = "Parsed user create request with email: {}";
    public static final String DOMAIN_MAPPING_SUCCESS = "Successfully mapped request to domain object";
    public static final String USER_CREATED_SUCCESS = "Successfully created user with id: {}";
    public static final String GET_RESPONSE_SUCCESS = "Successfully sent response for GET /users request";
    
    // Error logging templates
    public static final String DOMAIN_VALIDATION_FAILED = "Domain validation failed: {}";
    public static final String POST_REQUEST_ERROR = "Error processing POST /users request: {}";
    public static final String GET_REQUEST_ERROR = "Error processing GET /users request: {}";
    public static final String VALIDATION_FAILED = "Request validation failed: {}";
    
    // Exception handler logging templates
    public static final String VALIDATION_ERROR = "Validation error: {}";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation: {}";
    public static final String BUSINESS_VALIDATION_ERROR = "Business validation error: {}";
    public static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    
    // Error response messages
    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered";
    public static final String DATA_CONFLICT_OCCURRED = "Data conflict occurred";
    public static final String VALIDATION_FAILED_MESSAGE = "Request validation failed";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    
    // Domain use case logging templates
    public static final String STARTING_USER_REGISTRATION = "Starting user registration use case";
    public static final String GET_ALL_USERS_SUCCESS = "Successfully completed getAllUsers use case";
}
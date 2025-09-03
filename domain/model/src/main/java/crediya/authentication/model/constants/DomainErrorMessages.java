package crediya.authentication.model.constants;

public final class DomainErrorMessages {
    
    private DomainErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Email validation messages
    public static final String EMAIL_NULL_OR_EMPTY = "cannot be null or empty";
    public static final String EMAIL_INVALID_FORMAT = "invalid email format";
    
    // Salary validation messages
    public static final String SALARY_NULL = "cannot be null";
    public static final String SALARY_BELOW_MINIMUM = "must be at least 0";
    public static final String SALARY_ABOVE_MAXIMUM = "must not exceed 15,000,000";
    
    // User validation messages
    public static final String USER_NULL = "user cannot be null";
    
    // Business rule messages
    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered: %s";
    
    // Field validation templates
    public static final String VALIDATION_FAILED_TEMPLATE = "Validation failed for field '%s': %s";
}
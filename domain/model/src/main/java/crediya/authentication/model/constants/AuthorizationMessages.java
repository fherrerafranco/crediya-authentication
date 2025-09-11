package crediya.authentication.model.constants;

public final class AuthorizationMessages {
    
    // General authorization messages
    public static final String INVALID_AUTHORIZATION_CONTEXT = "Invalid authorization context";
    public static final String ROLE_PERMISSION_DENIED_TEMPLATE = "Role %s does not have permission %s";
    
    // Permission-specific messages
    public static final String CUSTOMER_LOAN_APPLICATION_SELF_ONLY = "Customers can only create loan applications for themselves";
    public static final String VIEW_OWN_LOAN_APPLICATION_ONLY = "Users can only view their own loan applications";
    public static final String INSUFFICIENT_PERMISSIONS = "Insufficient permissions to perform this action";
    
    // Role validation messages
    public static final String INVALID_ROLE_ID = "Invalid role ID: %d";
    public static final String INVALID_ROLE_NAME = "Invalid role name: %s";
    public static final String ROLE_TYPE_CANNOT_BE_NULL = "RoleType cannot be null";
    
    // Permission validation messages
    public static final String PERMISSION_CODE_CANNOT_BE_NULL = "Permission code cannot be null or empty";
    public static final String INVALID_PERMISSION_CODE = "Invalid permission code: %s";
    
    private AuthorizationMessages() {
        // Utility class - prevent instantiation
    }
}
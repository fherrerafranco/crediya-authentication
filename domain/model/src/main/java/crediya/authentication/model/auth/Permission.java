package crediya.authentication.model.auth;

import crediya.authentication.model.constants.AuthorizationMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    // User management permissions
    CREATE_USER("CREATE_USER", "Can create new users"),
    VIEW_ALL_USERS("VIEW_ALL_USERS", "Can view all users in the system"),
    UPDATE_USER("UPDATE_USER", "Can update user information"),
    DELETE_USER("DELETE_USER", "Can delete users"),
    
    // Loan application permissions
    CREATE_LOAN_APPLICATION("CREATE_LOAN_APPLICATION", "Can create loan applications"),
    VIEW_OWN_LOAN_APPLICATION("VIEW_OWN_LOAN_APPLICATION", "Can view own loan applications"),
    VIEW_ALL_LOAN_APPLICATIONS("VIEW_ALL_LOAN_APPLICATIONS", "Can view all loan applications"),
    APPROVE_LOAN_APPLICATION("APPROVE_LOAN_APPLICATION", "Can approve loan applications"),
    
    // System permissions
    VIEW_SYSTEM_HEALTH("VIEW_SYSTEM_HEALTH", "Can view system health information"),
    MANAGE_SYSTEM_CONFIG("MANAGE_SYSTEM_CONFIG", "Can manage system configuration");
    
    private final String code;
    private final String description;
    
    public static Permission fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException(AuthorizationMessages.PERMISSION_CODE_CANNOT_BE_NULL);
        }
        
        for (Permission permission : values()) {
            if (permission.getCode().equals(code.trim())) {
                return permission;
            }
        }
        
        throw new IllegalArgumentException(String.format(AuthorizationMessages.INVALID_PERMISSION_CODE, code));
    }
}
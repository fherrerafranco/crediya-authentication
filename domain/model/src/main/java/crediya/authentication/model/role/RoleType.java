package crediya.authentication.model.role;

import crediya.authentication.model.constants.AuthorizationMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    CUSTOMER(1, "CUSTOMER", "Customer with limited access to own resources"),
    ADVISOR(2, "ADVISOR", "Financial advisor with user management access"),
    ADMIN(3, "ADMIN", "System administrator with full access");
    
    private final Integer id;
    private final String name;
    private final String description;
    
    public static RoleType fromId(Integer id) {
        if (id == null) {
            return null;
        }
        
        for (RoleType roleType : values()) {
            if (roleType.getId().equals(id)) {
                return roleType;
            }
        }
        
        throw new IllegalArgumentException(String.format(AuthorizationMessages.INVALID_ROLE_ID, id));
    }
    
    public static RoleType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        for (RoleType roleType : values()) {
            if (roleType.getName().equalsIgnoreCase(name.trim())) {
                return roleType;
            }
        }
        
        throw new IllegalArgumentException(String.format(AuthorizationMessages.INVALID_ROLE_NAME, name));
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    public boolean isAdvisor() {
        return this == ADVISOR;
    }
    
    public boolean isCustomer() {
        return this == CUSTOMER;
    }
    
    public boolean hasAdministrativeAccess() {
        return this == ADMIN || this == ADVISOR;
    }
}
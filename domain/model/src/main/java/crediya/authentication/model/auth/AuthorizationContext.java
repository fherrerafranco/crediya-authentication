package crediya.authentication.model.auth;

import crediya.authentication.model.role.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AuthorizationContext {
    private final String userId;
    private final RoleType roleType;
    private final String targetResourceId; // Optional - for resource-level authorization
    private final String clientIp; // Optional - for audit/security
    
    public boolean hasRole(RoleType expectedRole) {
        return this.roleType != null && this.roleType.equals(expectedRole);
    }
    
    public boolean hasAnyRole(RoleType... expectedRoles) {
        if (this.roleType == null || expectedRoles == null) {
            return false;
        }
        
        for (RoleType role : expectedRoles) {
            if (this.roleType.equals(role)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isOwnerOf(String resourceId) {
        return this.userId != null && this.userId.equals(resourceId);
    }
    
    public boolean isAdministrative() {
        return this.roleType != null && this.roleType.hasAdministrativeAccess();
    }
    
    public boolean canAccessResource(String resourceId) {
        return isAdministrative() || isOwnerOf(resourceId);
    }
    
    public static AuthorizationContext of(String userId, RoleType roleType) {
        return AuthorizationContext.builder()
                .userId(userId)
                .roleType(roleType)
                .build();
    }
    
    public static AuthorizationContext of(String userId, RoleType roleType, String targetResourceId) {
        return AuthorizationContext.builder()
                .userId(userId)
                .roleType(roleType)
                .targetResourceId(targetResourceId)
                .build();
    }
}
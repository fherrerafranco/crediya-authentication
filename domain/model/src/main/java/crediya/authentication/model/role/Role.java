package crediya.authentication.model.role;

import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.constants.AuthorizationMessages;
import crediya.authentication.model.constants.RolePermissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class Role {
    private final Integer id;
    private final String name;
    private final String description;
    
    public RoleType getRoleType() {
        return RoleType.fromName(this.name);
    }
    
    public Set<Permission> getPermissions() {
        return RolePermissions.getPermissions(getRoleType());
    }
    
    public boolean hasPermission(Permission permission) {
        return RolePermissions.hasPermission(getRoleType(), permission);
    }
    
    public boolean isAdministrative() {
        RoleType roleType = getRoleType();
        return roleType != null && roleType.hasAdministrativeAccess();
    }
    
    public static Role fromRoleType(RoleType roleType) {
        if (roleType == null) {
            throw new IllegalArgumentException(AuthorizationMessages.ROLE_TYPE_CANNOT_BE_NULL);
        }
        
        return Role.builder()
                .id(roleType.getId())
                .name(roleType.getName())
                .description(roleType.getDescription())
                .build();
    }
}
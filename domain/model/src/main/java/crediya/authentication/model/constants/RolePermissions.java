package crediya.authentication.model.constants;

import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.role.RoleType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {
    
    private static final Map<RoleType, Set<Permission>> ROLE_PERMISSION_MAP = Map.of(
        RoleType.ADMIN, EnumSet.of(
            // All user management permissions
            Permission.CREATE_USER,
            Permission.VIEW_ALL_USERS,
            Permission.UPDATE_USER,
            Permission.DELETE_USER,
            
            // Admin can view and manage loan applications but NOT create them
            // (Loan applications are created by customers)
            Permission.VIEW_ALL_LOAN_APPLICATIONS,
            Permission.APPROVE_LOAN_APPLICATION,
            
            // System permissions
            Permission.VIEW_SYSTEM_HEALTH,
            Permission.MANAGE_SYSTEM_CONFIG
        ),
        
        RoleType.ADVISOR, EnumSet.of(
            // Limited user management
            Permission.CREATE_USER,
            Permission.VIEW_ALL_USERS,
            Permission.UPDATE_USER,
            
            // Advisor can view and approve loan applications but NOT create them
            // (Loan applications are created by customers)
            Permission.VIEW_ALL_LOAN_APPLICATIONS,
            Permission.APPROVE_LOAN_APPLICATION,
            
            // Basic system access
            Permission.VIEW_SYSTEM_HEALTH
        ),
        
        RoleType.CUSTOMER, EnumSet.of(
            // Own loan applications only
            Permission.CREATE_LOAN_APPLICATION,
            Permission.VIEW_OWN_LOAN_APPLICATION
        )
    );
    
    public static Set<Permission> getPermissions(RoleType roleType) {
        if (roleType == null) {
            return EnumSet.noneOf(Permission.class);
        }
        
        return ROLE_PERMISSION_MAP.getOrDefault(roleType, EnumSet.noneOf(Permission.class));
    }
    
    public static boolean hasPermission(RoleType roleType, Permission permission) {
        if (roleType == null || permission == null) {
            return false;
        }
        
        return getPermissions(roleType).contains(permission);
    }

}
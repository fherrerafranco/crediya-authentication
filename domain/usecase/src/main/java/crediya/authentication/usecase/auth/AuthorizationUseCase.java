package crediya.authentication.usecase.auth;

import crediya.authentication.model.auth.AuthorizationContext;
import crediya.authentication.model.auth.AuthorizationResult;
import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.constants.AuthorizationMessages;
import crediya.authentication.model.constants.RolePermissions;
import crediya.authentication.model.role.RoleType;
import crediya.authentication.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthorizationUseCase {
    
    private final RoleRepository roleRepository;
    
    /**
     * Main authorization method - checks if a user has permission to perform an action
     */
    public Mono<AuthorizationResult> authorize(String userId, Integer roleId, Permission permission) {
        return authorize(userId, roleId, permission, null);
    }
    
    /**
     * Authorization with target resource - for resource-level permissions
     */
    public Mono<AuthorizationResult> authorize(String userId, Integer roleId, Permission permission, String targetResourceId) {
        if (userId == null || roleId == null || permission == null) {
            return Mono.just(AuthorizationResult.denied(permission, AuthorizationMessages.INVALID_AUTHORIZATION_CONTEXT));
        }
        
        return roleRepository.findById(roleId)
                .map(role -> {
                    try {
                        RoleType roleType = RoleType.fromName(role.getName());
                        AuthorizationContext context = AuthorizationContext.builder()
                                .userId(userId)
                                .roleType(roleType)
                                .targetResourceId(targetResourceId)
                                .build();
                        
                        return performAuthorization(context, permission);
                    } catch (IllegalArgumentException e) {
                        return AuthorizationResult.denied(permission, 
                            String.format(AuthorizationMessages.INVALID_ROLE_NAME, role.getName()));
                    }
                })
                .defaultIfEmpty(AuthorizationResult.denied(permission, 
                    String.format(AuthorizationMessages.INVALID_ROLE_ID, roleId)));
    }
    
    /**
     * Direct authorization with RoleType (more efficient when role is already known)
     */
    public Mono<AuthorizationResult> authorize(AuthorizationContext context, Permission permission) {
        if (context == null || context.getRoleType() == null || permission == null) {
            return Mono.just(AuthorizationResult.denied(permission, AuthorizationMessages.INVALID_AUTHORIZATION_CONTEXT));
        }
        
        return Mono.fromCallable(() -> performAuthorization(context, permission));
    }
    
    private AuthorizationResult performAuthorization(AuthorizationContext context, Permission permission) {
        // Check basic permission
        boolean hasPermission = RolePermissions.hasPermission(context.getRoleType(), permission);
        
        if (!hasPermission) {
            return AuthorizationResult.denied(permission, 
                String.format(AuthorizationMessages.ROLE_PERMISSION_DENIED_TEMPLATE, 
                    context.getRoleType().getName(), permission.getCode()));
        }
        
        // Apply business rules
        return applyBusinessRules(context, permission);
    }
    
    private AuthorizationResult applyBusinessRules(AuthorizationContext context, Permission permission) {
        switch (permission) {
            case CREATE_LOAN_APPLICATION:
                // Customers can only create loan applications for themselves
                if (context.getRoleType() == RoleType.CUSTOMER && context.getTargetResourceId() != null) {
                    if (!context.isOwnerOf(context.getTargetResourceId())) {
                        return AuthorizationResult.denied(permission, 
                            AuthorizationMessages.CUSTOMER_LOAN_APPLICATION_SELF_ONLY);
                    }
                }
                break;
                
            case VIEW_OWN_LOAN_APPLICATION:
                // Users can only view their own loan applications (unless admin/advisor)
                if (!context.isAdministrative() && context.getTargetResourceId() != null) {
                    if (!context.isOwnerOf(context.getTargetResourceId())) {
                        return AuthorizationResult.denied(permission, 
                            AuthorizationMessages.VIEW_OWN_LOAN_APPLICATION_ONLY);
                    }
                }
                break;
                
            case UPDATE_USER:
            case DELETE_USER:
                // Additional rule: Users can only update/delete their own profile (unless admin)
                if (context.getRoleType() == RoleType.CUSTOMER && context.getTargetResourceId() != null) {
                    if (!context.isOwnerOf(context.getTargetResourceId())) {
                        return AuthorizationResult.denied(permission, 
                            AuthorizationMessages.INSUFFICIENT_PERMISSIONS);
                    }
                }
                break;
                
            default:
                // No additional business rules for this permission
                break;
        }
        
        return AuthorizationResult.authorized(permission);
    }
    
    // Convenience methods for common authorization checks
    
    public Mono<Boolean> canCreateUser(String userId, Integer roleId) {
        return authorize(userId, roleId, Permission.CREATE_USER)
                .map(AuthorizationResult::isAuthorized);
    }
    
    public Mono<Boolean> canViewAllUsers(String userId, Integer roleId) {
        return authorize(userId, roleId, Permission.VIEW_ALL_USERS)
                .map(AuthorizationResult::isAuthorized);
    }
    
    public Mono<Boolean> canCreateLoanApplication(String userId, Integer roleId, String targetUserId) {
        return authorize(userId, roleId, Permission.CREATE_LOAN_APPLICATION, targetUserId)
                .map(AuthorizationResult::isAuthorized);
    }
    
    public Mono<Boolean> canViewLoanApplication(String userId, Integer roleId, String loanApplicationUserId) {
        return authorize(userId, roleId, Permission.VIEW_OWN_LOAN_APPLICATION, loanApplicationUserId)
                .map(AuthorizationResult::isAuthorized);
    }
    
    public Mono<Boolean> canUpdateUser(String userId, Integer roleId, String targetUserId) {
        return authorize(userId, roleId, Permission.UPDATE_USER, targetUserId)
                .map(AuthorizationResult::isAuthorized);
    }
    
    public Mono<Boolean> canDeleteUser(String userId, Integer roleId, String targetUserId) {
        return authorize(userId, roleId, Permission.DELETE_USER, targetUserId)
                .map(AuthorizationResult::isAuthorized);
    }
}
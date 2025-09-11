package crediya.authentication.model.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AuthorizationResult {
    private final boolean authorized;
    private final String reason; // Optional - explanation for denial
    private final Permission permission; // The permission that was checked
    
    public static AuthorizationResult authorized(Permission permission) {
        return AuthorizationResult.builder()
                .authorized(true)
                .permission(permission)
                .build();
    }
    
    public static AuthorizationResult denied(Permission permission, String reason) {
        return AuthorizationResult.builder()
                .authorized(false)
                .permission(permission)
                .reason(reason)
                .build();
    }
    
    public static AuthorizationResult denied(Permission permission) {
        return denied(permission, "Access denied for permission: " + permission.getCode());
    }
    
    public boolean isAuthorized() {
        return authorized;
    }
    
    public boolean isDenied() {
        return !authorized;
    }
}
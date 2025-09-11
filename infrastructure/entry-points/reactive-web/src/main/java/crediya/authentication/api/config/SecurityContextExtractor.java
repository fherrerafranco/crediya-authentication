package crediya.authentication.api.config;

import crediya.authentication.api.constants.JwtConstants;
import crediya.authentication.model.auth.AuthorizationContext;
import crediya.authentication.model.constants.AuthorizationMessages;
import crediya.authentication.model.role.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class SecurityContextExtractor {
    
    // Use constants from JwtConstants for consistency
    private static final String CLIENT_IP_HEADER = "X-Forwarded-For";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String UNKNOWN_CLIENT_IP = "unknown";
    
    /**
     * Extracts authorization context from ServerWebExchange
     */
    public AuthorizationContext extractAuthorizationContext(ServerWebExchange exchange) {
        return extractAuthorizationContext(exchange, null);
    }
    
    /**
     * Extracts authorization context from ServerWebExchange with target resource ID
     */
    public AuthorizationContext extractAuthorizationContext(ServerWebExchange exchange, String targetResourceId) {
        try {
            String userId = extractUserId(exchange);
            String roleName = extractRoleName(exchange);
            RoleType roleType = extractRoleType(exchange);
            String clientIp = extractClientIp(exchange);
            
            log.debug("Extracting auth context - userId: {}, roleName: {}, roleType: {}", userId, roleName, roleType);
            
            if (userId == null || roleType == null) {
                log.warn("Invalid security context - userId: {}, roleName: {}, roleType: {}", userId, roleName, roleType);
                log.warn("Available exchange attributes: {}", exchange.getAttributes().keySet());
                return null;
            }
            
            log.debug("Successfully extracted authorization context for user: {}, role: {}", userId, roleType);
            
            return AuthorizationContext.builder()
                    .userId(userId)
                    .roleType(roleType)
                    .targetResourceId(targetResourceId)
                    .clientIp(clientIp)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error extracting authorization context: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Extracts user ID from the security context
     */
    public String extractUserId(ServerWebExchange exchange) {
        return (String) exchange.getAttributes().get(JwtConstants.USER_ID_ATTRIBUTE);
    }
    
    /**
     * Extracts role name from the security context
     */
    public String extractRoleName(ServerWebExchange exchange) {
        return (String) exchange.getAttributes().get(JwtConstants.ROLE_ATTRIBUTE);
    }
    
    /**
     * Extracts role type from the security context
     */
    public RoleType extractRoleType(ServerWebExchange exchange) {
        String roleName = extractRoleName(exchange);
        if (roleName == null) {
            return null;
        }
        
        try {
            return RoleType.fromName(roleName);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role name: {}", roleName);
            return null;
        }
    }
    
    /**
     * Extracts client IP from headers or connection info
     */
    public String extractClientIp(ServerWebExchange exchange) {
        // First check X-Forwarded-For header
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst(CLIENT_IP_HEADER);
        if (xForwardedFor != null && !xForwardedFor.trim().isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Fall back to remote address
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        
        return UNKNOWN_CLIENT_IP;
    }
    
    /**
     * Extracts user agent from headers
     */
    public String extractUserAgent(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(USER_AGENT_HEADER);
    }
    
    /**
     * Checks if the current user is the owner of a resource
     */
    public boolean isOwnerOf(ServerWebExchange exchange, String resourceUserId) {
        String currentUserId = extractUserId(exchange);
        return currentUserId != null && currentUserId.equals(resourceUserId);
    }
    
    /**
     * Checks if the current user has administrative privileges
     */
    public boolean hasAdministrativeAccess(ServerWebExchange exchange) {
        RoleType roleType = extractRoleType(exchange);
        return roleType != null && roleType.hasAdministrativeAccess();
    }
    
    /**
     * Checks if the current user can access a resource (admin or owner)
     */
    public boolean canAccessResource(ServerWebExchange exchange, String resourceUserId) {
        return hasAdministrativeAccess(exchange) || isOwnerOf(exchange, resourceUserId);
    }
}
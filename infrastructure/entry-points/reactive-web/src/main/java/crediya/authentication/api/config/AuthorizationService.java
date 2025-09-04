package crediya.authentication.api.config;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class AuthorizationService {
    
    // Role constants
    public static final int ADMIN_ROLE = 1;
    public static final int ADVISOR_ROLE = 2;
    public static final int CUSTOMER_ROLE = 3;
    
    public boolean hasAdminOrAdvisorRole(ServerWebExchange exchange) {
        Integer roleId = (Integer) exchange.getAttributes().get("roleId");
        return roleId != null && (roleId == ADMIN_ROLE || roleId == ADVISOR_ROLE);
    }
    
    public boolean hasCustomerRole(ServerWebExchange exchange) {
        Integer roleId = (Integer) exchange.getAttributes().get("roleId");
        return roleId != null && roleId == CUSTOMER_ROLE;
    }
    
    public boolean isOwnerOrAdmin(ServerWebExchange exchange, String resourceUserId) {
        String currentUserId = (String) exchange.getAttributes().get("userId");
        Integer roleId = (Integer) exchange.getAttributes().get("roleId");
        
        return (currentUserId != null && currentUserId.equals(resourceUserId)) ||
               (roleId != null && roleId == ADMIN_ROLE);
    }
}
package crediya.authentication.api.config;

import crediya.authentication.model.auth.AuthorizationContext;
import crediya.authentication.model.auth.AuthorizationResult;
import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.role.RoleType;
import crediya.authentication.usecase.auth.AuthorizationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Infrastructure layer authorization service that bridges web layer with domain authorization
 * This service extracts security context from HTTP requests and delegates to domain use cases
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    
    private final AuthorizationUseCase authorizationUseCase;
    private final SecurityContextExtractor securityContextExtractor;
    
    
    /**
     * Main authorization method - checks permission for current user
     */
    public Mono<AuthorizationResult> authorize(ServerWebExchange exchange, Permission permission) {
        return authorize(exchange, permission, null);
    }
    
    /**
     * Authorization with target resource - for resource-level permissions  
     */
    public Mono<AuthorizationResult> authorize(ServerWebExchange exchange, Permission permission, String targetResourceId) {
        log.debug("Starting authorization check for permission: {} with targetResourceId: {}", permission, targetResourceId);
        
        AuthorizationContext context = securityContextExtractor.extractAuthorizationContext(exchange, targetResourceId);
        
        if (context == null) {
            log.warn("Failed to extract authorization context from exchange for permission: {}", permission);
            return Mono.just(AuthorizationResult.denied(permission, "Invalid security context"));
        }
        
        log.debug("Authorization context extracted, delegating to use case for permission: {}", permission);
        return authorizationUseCase.authorize(context, permission)
                .doOnNext(result -> log.debug("Authorization result for permission {}: {}", permission, result.isAuthorized()));
    }
    
    /**
     * Quick boolean check for permissions
     */
    public Mono<Boolean> hasPermission(ServerWebExchange exchange, Permission permission) {
        return authorize(exchange, permission)
                .map(AuthorizationResult::isAuthorized);
    }
}